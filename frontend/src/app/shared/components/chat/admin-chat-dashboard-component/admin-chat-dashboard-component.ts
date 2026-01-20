import { Component, inject, signal, computed, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatMessageService } from '../../../../core/services/chatMessage.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ChatWindowComponent } from '../chat-window-component/chat-window-component';
import { ChatSummaryDTO } from '../../../../models/dtos';
import { catchError, merge, of, Subject, switchMap, timer } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  faArrowRightFromBracket,
  faCommentDots,
  faUserPlus,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-admin-chat-dashboard',
  standalone: true,
  imports: [CommonModule, ChatWindowComponent, FontAwesomeModule],
  templateUrl: './admin-chat-dashboard-component.html',
})
export class AdminChatDashboardComponent {
  private chatService = inject(ChatMessageService);
  private authService = inject(AuthService);
  currentAdminId = this.authService.currentUserId();
  private refresh$ = new Subject<void>();

  icons = {
    faUserPlus,
    faArrowRightFromBracket,
    faCommentDots,
  };

  // 2. LISTA REATTIVA (Sostituisce ngOnInit e polling manuale)
  // Combiniamo il Timer (ogni 10s) con il Refresh Manuale
  chatList = toSignal(
    merge(
      timer(0, 10000), // Parte subito (0) e ripete ogni 10s
      this.refresh$ // Emette quando chiamiamo this.refresh$.next()
    ).pipe(
      // Ogni volta che uno dei due emette, chiamiamo il backend
      switchMap(() =>
        this.chatService.getChatSummaries().pipe(
          // Gestiamo l'errore QUI per non rompere l'intero flusso del polling
          catchError((err) => {
            console.error('Errore polling chat:', err);
            return of([]); // Ritorna lista vuota in caso di errore
          })
        )
      )
    ),
    { initialValue: [] as ChatSummaryDTO[] } // Valore iniziale prima della risposta
  );

  // Stato Locale
  selectedChatId = signal<string | null>(null);

  // Computed: Recupera l'oggetto chat selezionato dalla lista
  selectedChat = computed(() =>
    this.chatList().find((c) => c.chatUserId === this.selectedChatId())
  );

  canWrite = computed(() => {
    const chat = this.selectedChat();
    return chat?.status === 'ASSIGNED' && chat?.assignedAdminId === this.currentAdminId;
  });

  // Computed: Info per l'header
  assignmentInfo = computed(() => {
    const chat = this.selectedChat();
    if (!chat) return '';
    if (chat.status === 'FREE' && chat.waitingForReply) return 'IN ATTESA DI RISPOSTA';
    if (chat.assignedAdminId === this.currentAdminId) return 'IN TUA GESTIONE';
    return `GESTITA DA: ${chat.assignedAdminId}`;
  });

  // --- AZIONI ---

  selectChat(chatId: string) {
    this.selectedChatId.set(chatId);
  }

  takeCharge() {
    const chatId = this.selectedChatId();
    if (!chatId) return;

    this.chatService.takeCharge(chatId).subscribe({
      next: () => {
        this.refresh$.next();
      },
      error: (err) => {
        alert('Impossibile assegnare: ' + err.message);
        this.refresh$.next();
      },
    });
  }

  releaseChat() {
    const chatId = this.selectedChatId();
    if (!chatId) return;

    this.chatService.releaseChat(chatId).subscribe({
      next: () => {
        this.refresh$.next();
      },
      error: (err) => {
        alert('Errore rilascio: ' + err.message), this.refresh$.next();
      },
    });
  }
}
