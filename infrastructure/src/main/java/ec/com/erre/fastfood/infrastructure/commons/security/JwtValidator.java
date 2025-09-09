package ec.com.erre.fastfood.infrastructure.commons.security;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

import ec.com.erre.fastfood.infrastructure.commons.exceptions.RemoteExecutionException;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtValidator {

	private JWKSet jwkSet;
	private KeycloakProperties keycloakProperties;

	public JwtValidator(KeycloakProperties keycloakProperties)
			throws MalformedURLException, IOException, ParseException {
		this.keycloakProperties = keycloakProperties;
		initJwks();
	}

	private void initJwks() throws MalformedURLException, IOException, ParseException {
		InputStream stream = new URL(keycloakProperties.getJwkSetUri()).openStream();
		this.jwkSet = JWKSet.load(stream);
		stream.close();
	}

	public String extraerUsuarioConectado(String jwtToken) throws RemoteExecutionException {
		if (jwtToken == null) {
			throw new RemoteExecutionException(401, "Mensaje no contiene JWT token");
		}

		try {

			SignedJWT signedJWT = SignedJWT.parse(jwtToken);
			JWSHeader header = signedJWT.getHeader();
			String kid = header.getKeyID();

			JWK jwk = obtenerJwk(kid);

			validarToken(signedJWT, jwk);

			return signedJWT.getJWTClaimsSet().getStringClaim("preferred_username");
		} catch (ParseException | JOSEException e) {
			throw new RemoteExecutionException(500, e.getMessage());
		}
	}

	private JWK obtenerJwk(String kid) throws RemoteExecutionException {
		JWK jwk = jwkSet.getKeyByKeyId(kid);
		if (jwk == null) {
			throw new RemoteExecutionException(401, "Llave publica no encontrada en kid: " + kid);
		}
		return jwk;
	}

	private void validarToken(SignedJWT signedJWT, JWK jwk)
			throws JOSEException, ParseException, RemoteExecutionException {
		RSAKey rsaKey = (RSAKey) jwk;
		RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
		JWSVerifier verifier = new RSASSAVerifier(publicKey);

		if (!signedJWT.verify(verifier)) {
			throw new RemoteExecutionException(401, "Token no es valido");
		}

		Date expires = signedJWT.getJWTClaimsSet().getExpirationTime();
		if (expires == null || new Date().after(expires)) {
			throw new RemoteExecutionException(401, "Token a expirado");
		}

	}

}
