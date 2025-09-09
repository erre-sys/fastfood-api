package ec.com.erre.fastfood.infrastructure.commons.exceptions;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse implements Serializable {
	private String backendMessage;
	private String message;
	private String url;
	private String method;
	private int status;
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private Date timestamp;

	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = new Date();
	}
}
