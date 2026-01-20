import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import { ChatMessageInputDTO, ChatMessageOutputDTO, ChatSummaryDTO, ErrorResponse } from '../../models/dtos';
import { AuthService } from './auth.service';
import { ChatMessage } from '../../models/chat-message.model';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ChatMessageService {
  private authService = inject(AuthService);
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/chat/';
  brokerURL: string = `${environment.baseUrl}/ws-chat`;
  stompClient: any;
  
  public messagesSignal: WritableSignal<ChatMessage[]> = signal([]);

  connect(chatUserId: string) {
    let ws = new SockJS(this.brokerURL);
    this.stompClient = Stomp.over(ws);
    console.log('connected to websocket');

    let token = this.authService.getToken();

    this.stompClient.connect(
      { Authorization: 'Bearer ' + token },
      () => {
        this.stompClient.subscribe(`/topic/user/${chatUserId}`, (sdkMessage: any) => {
          this.onMessageReceived(sdkMessage);
        });
      },
      (error: ErrorResponse) => {
        console.log(error.error.message);
      }
    );
  }

  onMessageReceived(sdkMessage: any) {
    const message = JSON.parse(sdkMessage.body) as ChatMessageInputDTO;
    const chatMessage = new ChatMessage(message);
    this.messagesSignal.update((messages) => [...messages, chatMessage]);
  }

  sendMessage(message: ChatMessageOutputDTO): void {
    this.stompClient.send('/app/chat.send', {}, JSON.stringify(message));
  }

  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    this.messagesSignal.set([]);
    console.log('Disconnected');
  }

  getHistory(chatUserId: string): void {
    this.http.get<ChatMessageInputDTO[]>(`${this.apiUrl}history/${chatUserId}`)
      .pipe(
        catchError(this.handleError)
      )
      .subscribe({
        next: (data) => {
          const chatMessages = data.map((msg: ChatMessageInputDTO) => new ChatMessage(msg));
          this.messagesSignal.set(chatMessages);
        },
        error: (err) => console.error('Errore recupero storico:', err)
      });
  }

  getChatSummaries(): Observable<ChatSummaryDTO[]> {
    return this.http.get<ChatSummaryDTO[]>(`${this.apiUrl}summaries`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Tenta di prendere in carico una chat (POST /api/chat/assign/{id}).
   * Restituisce un Observable per permettere al Component di gestire Successo o Errore (409).
   */
  takeCharge(clubManagerId: string): Observable<string> {
    // Nota: passiamo un body vuoto {} perché è una POST
    // responseType: 'text' è necessario perché il backend restituisce una stringa semplice, non un JSON
    return this.http.post(`${this.apiUrl}assign/${clubManagerId}`, {}, { responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Rilascia una chat precedentemente presa in carico (POST /api/chat/release/{id}).
   */
  releaseChat(clubManagerId: string): Observable<string> {
    return this.http.post(`${this.apiUrl}release/${clubManagerId}`, {}, { responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Gestione base degli errori HTTP
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Errore sconosciuto';
    if (error.error instanceof ErrorEvent) {
      // Errore Client-side
      errorMessage = `Errore: ${error.error.message}`;
    } else {
      // Errore Server-side
      // Qui intercettiamo il famoso 409 Conflict o le nostre eccezioni custom
      errorMessage = error.error || `Codice server: ${error.status}, messaggio: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
