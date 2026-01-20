import { Role } from '../enums/role.enum';
import { ChatMessageInputDTO } from './dtos';

export class ChatMessage {
  private _id: string;
  private _chatUserId: string;
  private _senderId: string;
  private _senderRole: Role;
  private _content: string;
  private _timestamp: string;

  constructor(data: ChatMessageInputDTO) {
    this._id = data.id;
    this._chatUserId = data.chatUserId;
    this._senderId = data.senderId;
    this._content = data.content;
    this._senderRole = data.senderRole;
    this._timestamp = data.timestamp;
  }

  get id(): string {
    return this._id;
  }

  get chatUserId(): string {
    return this._chatUserId;
  }

  set chatUserId(value: string) {
    this._chatUserId = value;
  }

  get senderId(): string {
    return this._senderId;
  }

  set senderId(value: string) {
    this._senderId = value;
  }

  get senderRole(): Role {
    return this._senderRole;
  }

  set senderRole(value: Role) {
    this._chatUserId = value;
  }

  get content(): string {
    return this._content;
  }

  set content(value: string) {
    this._content = value;
  }

  get timestamp(): string {
    return this._timestamp;
  }

  set timestamp(value: string) {
    this._timestamp = value;
  }

  public toDTO(): ChatMessageInputDTO {
    const chatMessageIntutDTO: ChatMessageInputDTO = {
      id: this.id,
      chatUserId: this.chatUserId,
      senderId: this._senderId,
      content: this.content,
      senderRole: this.senderRole,
      timestamp: this.timestamp
    };
    return chatMessageIntutDTO;
  }
}
