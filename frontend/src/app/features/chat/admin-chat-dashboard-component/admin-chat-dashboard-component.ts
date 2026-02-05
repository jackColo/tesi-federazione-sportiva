import { Component, inject, signal, computed, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatMessageService } from '../../../core/services/chatMessage.service';
import { AuthService } from '../../../core/services/auth.service';
import { ChatWindowComponent } from '../chat-window-component/chat-window-component';
import { ChatSummaryDTO } from '../../../models/dtos';
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

  // Tengo aggiornata la lista delle chat combiniamo il Timer del polling (ogni 10s) con il Refresh Manuale che avviene ogni volta 
  // che l'admin tenta di prendere in carico o rilasciare una chat. In questo modo, se un altro admin prende in carico una chat, 
  // entro massimo 10s non la vedrò più come in attesa.
  chatList = toSignal(
    merge(
      timer(0, 10000),
      this.refresh$
    ).pipe(
      // Ogni volta che uno dei due emette un segnale, chiamiamo il backend
      switchMap(() =>
        this.chatService.getChatSummaries().pipe(
          // la gestione dell'errore qui non rompe l'intero flusso del polling
          catchError((err) => {
            console.error('Errore polling chat:', err);
            return of([]); 
          })
        )
      )
    ),
    { initialValue: [] as ChatSummaryDTO[] } 
  );

  selectedChatId = signal<string | null>(null);

  selectedChat = computed(() =>
    this.chatList().find((c) => c.chatUserId === this.selectedChatId())
  );

  canWrite = computed(() => {
    const chat = this.selectedChat();
    return chat?.status === 'ASSIGNED' && chat?.assignedAdminId === this.currentAdminId;
  });

  assignmentInfo = computed(() => {
    const chat = this.selectedChat();
    if (!chat) return '';
    if (chat.status === 'FREE' && chat.waitingForReply) return 'IN ATTESA DI RISPOSTA';
    if (chat.assignedAdminId === this.currentAdminId) return 'IN TUA GESTIONE';
    return `GESTITA DA: ${chat.assignedAdminId}`;
  });

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
