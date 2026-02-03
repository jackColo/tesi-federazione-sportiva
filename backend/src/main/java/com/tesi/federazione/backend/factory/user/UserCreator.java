package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.User;
/**
 * Interfaccia base per il Factory Method di creazione utenti.
 * Definisce il contratto che tutti i creatori specifici devono implementare.
 * Permette di disaccoppiare la logica d'istanziazione delle sottoclassi di User
 * dal servizio principale.
 */
public interface UserCreator {
    /**
     * Crea un'istanza specifica di User popolando i campi propri della sottoclasse.
     * @param dto Oggetto di trasferimento dati contenente le informazioni per la creazione.
     * @return L'entità User (o una sua sottoclasse) istanziata ma non ancora salvata su DB.
     */
    User createUser(CreateUserDTO dto);

    /**
     * Aggiorna un'istanza specifica di User popolando i campi propri della sottoclasse.
     * @param user Utente di partenza da aggiornare
     * @param dto Oggetto di trasferimento dati contenente le informazioni per la creazione.
     */
    void updateUser(User user, CreateUserDTO dto);
}
