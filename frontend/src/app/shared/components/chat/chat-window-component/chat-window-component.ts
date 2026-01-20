import {
  Component,
  OnDestroy,
  effect,
  inject,
  input,
  signal,
  Signal,
  ViewChild,
  ElementRef,
  untracked,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { ChatMessageService } from '../../../../core/services/chatMessage.service';
import { ChatMessage } from '../../../../models/chat-message.model';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-window-component.html',
})
export class ChatWindowComponent implements OnDestroy {
  private chatService = inject(ChatMessageService);
  private authService = inject(AuthService);
  contactName = input<string>('Assistenza');

  @ViewChild('chatContainer') private chatContainer!: ElementRef;

  chatUserId = input.required<string>();
  readOnly = input<boolean>(false);

  currentUserId = this.authService.currentUserId;
  messages: Signal<ChatMessage[]> = this.chatService.messagesSignal;
  inputText = signal('');

  constructor() {
    effect((onCleanup) => {
      const id = this.chatUserId();

      if (!id) return;

      console.log(`[ChatWindow] Avvio connessione per chat: ${id}`);
      this.chatService.getHistory(id);
      this.chatService.connect(id);

      onCleanup(() => {
        console.log(`[ChatWindow] Pulizia connessione precedente (ID era: ${id})`);
        this.chatService.disconnect();
      });
    });
    
    effect(() => {
      // Leggiamo il segnale: ogni volta che 'messages()' cambia, questo effetto parte.
      const msgs = this.messages();

      // Usiamo untracked per evitare dipendenze circolari o ri-esecuzioni inutili
      untracked(() => {
        // Usiamo un setTimeout per dare tempo ad Angular di aggiornare il DOM (renderizzare il nuovo messaggio)
        setTimeout(() => {
          this.scrollToBottom();
        }, 100);
      });
    });
  }

  private scrollToBottom(): void {
    try {
      if (this.chatContainer) {
        this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
      }
    } catch (err) {
      console.error('Errore durante lo scroll:', err);
    }
  }

  isMe(senderId: string) {
    return senderId === this.currentUserId();
  }

  sendMessage() {
    if (!this.inputText().trim() || this.readOnly()) return;

    this.chatService.sendMessage({
      chatUserId: this.chatUserId(),
      message: this.inputText(),
    });
    this.inputText.set('');
  }

  ngOnDestroy() {
    this.chatService.disconnect();
  }
}
