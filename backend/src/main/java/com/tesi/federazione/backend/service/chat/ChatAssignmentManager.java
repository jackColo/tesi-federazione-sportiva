package com.tesi.federazione.backend.service.chat;

import com.tesi.federazione.backend.exception.ResourceConflictException;
import com.tesi.federazione.backend.model.ChatSession;
import com.tesi.federazione.backend.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe di metodi per gestire la presa in carico e il rilascio delle chat da parte degli amministratori.
 * Gestisce le richieste concorrenti da parte degli amministratori tramite lock sugli accessi al chatRepository.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAssignmentManager {

    private final ChatSessionRepository chatSessionRepository;

    // Mappa per associare a un admin un lock quando prende in carico una chat, per evitare che ne prenda in carico altre
    private final ConcurrentHashMap<String, ReentrantLock> adminLocks = new ConcurrentHashMap<>();

    // Mappa per associare a una chat un lock, per evitare che più admin possano richiedere di prenderla in carico in contemporanea
    private final ConcurrentHashMap<String, ReentrantLock> chatLocks = new ConcurrentHashMap<>();

    /**
     * Metodo per verificare se sia possibile assegnare una specifica chat ad uno specifico club manager:
     * - L'admin non deve avere altre chat in corso
     * - La chat non deve essere stata presa in carico da nessuno
     * @param clubManagerId Id del club manager (utilzzato per identificare la chat)
     * @param adminId Id dell'admin a cui si vuole assegnare la chat
     */
    public void assignChat(String clubManagerId, String adminId) {

        // Verifico se nella mappa esiste già un'istanza del lock per l'adminId richiesto, altrimenti la creo
        ReentrantLock adminLock = adminLocks.computeIfAbsent(adminId, k -> new ReentrantLock());

        try {
            // Provo ad acquisire il lock, se non riesco in 2 secondi considero l'admin come occupato (con il timer evito Deadlock)
            if (!adminLock.tryLock(2, TimeUnit.SECONDS)) {
                throw new RuntimeException("Impossibile assegnare la chat, l'amministratore risulta già occupato.");
            }

            try {
                // Ottenuto il lock, verifico la presenza a DB di chat attive per l'admin
                if (chatSessionRepository.findByAdminIdAndActiveTrue(adminId).isPresent()) {
                    throw new ResourceConflictException("Questo admin sta già gestendo un'altra conversazione!");
                }

                // Verifico se nella mappa esiste già un'istanza del lock per la chat da acquisire, altrimenti la creo
                ReentrantLock clubLock = chatLocks.computeIfAbsent(clubManagerId, k -> new ReentrantLock());
                // Cerco di acquisire il lock per poter accedere al chatSessionRepository e verificare la presenza
                // di sessioni attive relative alla chat richiesta (se il lock è stato preso da un altro thread questo
                // passa allo stato di 'WAITING' finchè non viene eseguito .unlock() dalla risorsa che lo possedeva
                // e il sistema non sceglie questo tra i thread che sono in 'WAITING' per risvegliarlo e passargli il lock()
                clubLock.lock();

                try {
                    // Ottenuto il lock, verifico la presenza di sessioni attive relative alla chat che si sta cercando di assegnare
                    if (chatSessionRepository.findByClubManagerIdAndActiveTrue(clubManagerId).isPresent()) {
                        throw new ResourceConflictException("Questa chat è già stata presa in carico.");
                    }

                    // Dopo i controlli creo una nuova sessione impostandola come attiva
                    ChatSession session =  new ChatSession();
                    session.setClubManagerId(clubManagerId);
                    session.setAdminId(adminId);
                    session.setActive(true);

                    chatSessionRepository.save(session);
                    log.info("Nuova sessione avviata: Admin {} -> Club {}", adminId, clubManagerId);

                } finally {
                    clubLock.unlock();
                }

            } finally {
                adminLock.unlock();
            }
        // Avvolgo tutte le operazioni che istanziano dei lock in un try catch di InterruptedException, eccezione
            // lanciata dal metodo tryLock() se il thread corrente viene interrotto mentre è in attesa di acquisire il lock.
        // Questa eccezione risveglia i thread che erano stati segnati come "in attesa", catturando l'errore e ripristinando
            // lo stato "in attesa" del thread faccio in modo che chi lo aveva istanziato sappia che era stato interrotto e
            // gestisca correttamente la chiusura del task.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operazione interrotta", e);
        }
    }

    /**
     * Gestisco il rilascio della chat da parte degli amministratori di club.
     * @param clubManagerId Id del club manager associato alla chat
     */
    public void releaseChat(String clubManagerId) {
        // Verifico se nella mappa esiste già un'istanza del lock per la chat da acquisire, altrimenti la creo
        ReentrantLock clubLock = chatLocks.computeIfAbsent(clubManagerId, k -> new ReentrantLock());
        // Cerco di acquisire il lock per poter accedere al chatSessionRepository e modificare lo stato della chat
        clubLock.lock();
        try {
            chatSessionRepository.findByClubManagerIdAndActiveTrue(clubManagerId)
                    .ifPresent(session -> {
                        session.setActive(false);
                        chatSessionRepository.save(session);
                        log.info("Sessione chiusa per il club {}", clubManagerId);
                    });
        } finally {
            clubLock.unlock();
        }
    }

    /**
     * Verifico l'id dell'admin che ha attualmente attiva una sessione per la chat con il club manager
     * @param clubManagerId Id del club manager associato alla chat
     * @return Id dell'admin con la sessione attiva o null se non ci sono sessioni attive per la chat con il club manager indicato
     */
    public String getCurrentAdminForClubManager(String clubManagerId) {
        return chatSessionRepository.findByClubManagerIdAndActiveTrue(clubManagerId)
                .map(ChatSession::getAdminId)
                .orElse(null);
    }
}