import { Component, inject } from '@angular/core';
import { ChatWindowComponent } from './chat-window-component/chat-window-component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-club-chat-page',
  standalone: true,
  imports: [ChatWindowComponent],
  template: `
    <div
      class="flex h-[calc(100vh-400px)] w-full bg-gray-100 overflow-hidden font-sans rounded-xl border border-gray-200"
    >
      <div class="py-8 px-16 flex-1 flex flex-col">
        <h1 class="text-2xl font-bold mb-2 text-gray-700">Assistenza tecnica</h1>
        <p class="text-sm italic mb-4 text-gray-500">Un operatore ti risponderà al più presto...</p>
        @if(currentUserId(); as id) {
        <app-chat-window [chatUserId]="id" [readOnly]="false"> </app-chat-window>
        }
      </div>
    </div>
  `,
})
export class ClubChatPageComponent {
  private authService = inject(AuthService);
  currentUserId = this.authService.currentUserId;
}
