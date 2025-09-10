package ec.com.erre.fastfood.infrastructure.commons.jms;

import com.fasterxml.jackson.core.JsonProcessingException;

import ec.com.erre.fastfood.domain.commons.exceptions.EntidadNoEncontradaException;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;

public interface Servicio {
	String execute(TextMessage textMessage) throws JsonProcessingException, JMSException, EntidadNoEncontradaException;

	boolean requiresAuthentication();
}
