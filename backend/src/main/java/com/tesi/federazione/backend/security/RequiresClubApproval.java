package com.tesi.federazione.backend.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotazione personalizzata che serve per marcare i metodi che richiedono una verifica
 * sullo stato di approvazione del Club gestito dal club manager che sta facendo la richiesta:
 * l'affiliazione del club dovrà essere stata accettata almeno la prima volta (il blocco è
 * specifico per richieste che potrebbero essere effettuate al primo accesso del club manager)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresClubApproval {
}
