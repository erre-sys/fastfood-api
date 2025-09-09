package ec.com.erre.fastfood.infrastructure.commons.jms;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

import ec.com.erre.fastfood.infrastructure.commons.exceptions.ErrorResponse;
import ec.com.erre.fastfood.infrastructure.commons.exceptions.RemoteExecutionException;
import ec.com.erre.fastfood.infrastructure.commons.security.ConnectedUser;
import ec.com.erre.fastfood.infrastructure.commons.security.JwtValidator;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class JmsClient {

	private JmsTemplate jmsTemplate;

	private JwtValidator jwtValidator;

	public JmsClient(JmsTemplate jmsTemplate, JwtValidator jwtValidator)
			throws MalformedURLException, IOException, ParseException {
		this.jmsTemplate = jmsTemplate;
		this.jwtValidator = jwtValidator;
	}

	public <T, R> R sendAndWaitForResponse(T request, Class<R> responseClass, String requestQueue, String replyQueue,
			Optional<String> version, String operacion, int timeOutInMillis)
			throws RemoteExecutionException, JsonProcessingException, JMSException {
		ObjectMapper mapper = new ObjectMapper();
		String correllationId = UUID.randomUUID().toString();

		String objectJson = mapper.writeValueAsString(request);
		MessageCreator messageCreator = new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JmsException, JMSException {
				TextMessage message = session.createTextMessage(objectJson);
				message.setJMSCorrelationID(correllationId);
				message.setStringProperty("jwt", ConnectedUser.token.get());
				message.setStringProperty("operacion", operacion);
				if (version.isPresent()) {
					message.setStringProperty("X-API-VERSION", version.get());
				}
				ThreadContext.put("correlationId", message.getJMSCorrelationID());
				log.info("Enviando mensaje: " + requestQueue + " - " + message.getJMSCorrelationID());
				return message;
			}
		};

		jmsTemplate.setExplicitQosEnabled(true);
		jmsTemplate.setTimeToLive(timeOutInMillis);
		jmsTemplate.send(requestQueue, messageCreator);
		log.info("Esperando respuesta");

		jmsTemplate.setReceiveTimeout(timeOutInMillis);

		log.info("Recibiendo respuesta: " + correllationId);

		Message reply = jmsTemplate.receiveSelected(replyQueue, "JMSCorrelationID='" + correllationId + "'");

		if (reply != null && reply.getBooleanProperty("SUCCESS")) {
			if (responseClass == String.class) {
				return responseClass.cast(reply.getBody(String.class));
			}
			String stringResponse = reply.getBody(String.class);
			if (stringResponse != null && !stringResponse.isEmpty()) {
				return mapper.readValue(stringResponse, responseClass);
			}
			return null;
		}

		String message = reply == null ? "{\"message\": \"Error ejecutando la operacion remota\"}"
				: reply.getBody(String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		ErrorResponse errorResponse = objectMapper.readValue(message, ErrorResponse.class);
		throw new RemoteExecutionException(errorResponse.getStatus(), errorResponse.getMessage());

	}

	public void executeAndResponse(final Message message, Session session, String replyQueue)
			throws JMSException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonResponse = null;
		boolean success = false;
		if (message instanceof TextMessage) {
			String operacion = message.getStringProperty("operacion");
			TextMessage textMessage = (TextMessage) message;
			ThreadContext.put("correlationId", textMessage.getJMSCorrelationID());
			log.info("Mensaje recibido");
			String jwtToken = textMessage.getStringProperty("jwt");
			try {
				Servicio operation = null;
				if (operation.requiresAuthentication()) {
					String extractedUsername = jwtValidator.extraerUsuarioConectado(jwtToken);
					ConnectedUser.userName.set(extractedUsername);
				}
				jsonResponse = operation.execute(textMessage);
				success = true;
			} catch (RemoteExecutionException e) {
				ErrorResponse error = new ErrorResponse(e.getStatusCode(), e.getMessage());
				jsonResponse = objectMapper.writeValueAsString(error);
			} catch (Exception e) {
				e.printStackTrace();
				ErrorResponse error = new ErrorResponse(500, e.getMessage());
				jsonResponse = objectMapper.writeValueAsString(error);
			}
		} else {
			jsonResponse = "Mensaje debe ser de tipo TextMessage";
		}

		Message responseMessage = session.createTextMessage(jsonResponse);
		responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
		responseMessage.setBooleanProperty("SUCCESS", success);
		this.jmsTemplate.send(replyQueue, s -> responseMessage);

	}
}
