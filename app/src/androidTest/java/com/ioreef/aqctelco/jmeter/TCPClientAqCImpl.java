package com.ioreef.aqctelco.jmeter;

import org.apache.jmeter.protocol.tcp.sampler.TCPClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Classe du client TCP à utiliser avec JMeter pour tester Morix.
 * 
 * <p>Peut être utilisé pour tester le serveur Morix Java 
 * implémenté avec BufferedReader et BufferedWriter.</p>
 * 
 * @version 3.0
 * @author Matthias Brun
 * 
 * @see org.apache.jmeter.protocol.tcp.sampler.TCPClient
 * 
 */
public class TCPClientAqCImpl implements TCPClient
{
	/** 
	 * Le caractère de fin-de-ligne/fin-de-message (end-of-line/end-of-message).
	 */
	private byte eolByte = (byte) '\n';


	/**
	 * Constructeur d'un client TCP à utiliser avec JMeter.
	 */
	public TCPClientAqCImpl()
	{
		super();
	}


	/**
	 * Méthode invoquée au démarrage du thread (JMeter > 2.3.2).
	 */
	public void setupTest(){
		// rien à faire.
	}
	
	/**
	 * Méthode invoquée à la terminaison du thread (JMeter > 2.3.2).
	 */
	public void teardownTest() 
	{
		// rien à faire.
	}

	/**
	 * Écrire une chaîne de caractères sur un flux d'écriture.
	 *
	 * <p>Une chaîne vide ne sera pas écrite.</p>
	 * <p>La chaîne écrite s'arrête avant le premier retour à la ligne (<tt>\n</tt>),
	 * si un retour à la ligne est présent.</p>
	 * <p>Remarque : l'envoi d'un chaîne vide est ainsi possible en saisissant seulement un retour à la ligne.</p> 
	 * 
	 * @param os le flux d'écriture de la socket. 
	 * @param s la chaîne de caractères.
	 */
	@Override
	public void write(OutputStream os, String s) 
	{
		try {
			if (s.length() != 0) {
				final BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));

				// Le message s'arrête avant le premier retour à la ligne, 
				// si un retour à la ligne est présent.  
				int limite = 0;
				while ((limite < s.length()) 
					&& (s.charAt(limite) != (char) this.eolByte)) {
					limite++; 
				}
				
				buffer.write(s, 0, limite);
				buffer.newLine();
				buffer.flush();
			}
		}
		catch (IOException ex) {
			System.err.println("Problème d'écriture sur le flux.");
			System.err.println(ex.getMessage());
		}
	}


 	/**
	 * Lire des chaînes de caractères sur un flux de lecture.
	 *
	 * <p>La lecture se fait jusqu'à la levée d'une interruption d'expiration (timeout, sortie attendue)
	 * ou que la lecture retourne <tt>null</tt> (coupure de connexion).</p>
	 * 
	 * @param is le flux de lecture de la socket. 
	 * @return s la chaîne de caractères.
	 */
	@Override
	public String read(InputStream is)
	{
		BufferedReader buffer;
		String s = "";
		
		try {
			buffer = new BufferedReader(new InputStreamReader(is, "UTF8"));

			final String eol = Character.toString((char) this.eolByte);

			String sr; 

			while ((sr = buffer.readLine()) != null) {
				s = s.concat(sr.concat(eol));
			}
		}
		catch (InterruptedIOException ex) {
			// Sortie attendue, par interruption de l'expiration (timeout) (JDK 1.3).
			// Remarque : 
			// Utilisation possible de SocketTimeoutException 
			// (extends InterruptedIOException) pour JDK 1.4.
			System.out.print("[info] TCPClientMorix : Interruption d'expiration sur le flux ");
			System.out.println("(" + ex.getMessage() + ")");
		}
		catch (UnsupportedEncodingException ex) {
			System.err.println("Problème de lecture sur le flux, encodage UTF8 non supporté.");
			System.err.println(ex.getMessage());
		}
		catch (IOException ex) {
			System.err.println("Problème de lecture sur le flux.");
			System.err.println(ex.getMessage());
		}
		return s;
	}


	/**
	 * Transférer un flux de lecture sur un flux d'écriture.
	 * 
	 * @param os le flux d'écriture d'une socket. 
	 * @param is le flux de lecture d'une socket.
	 */
	@Override
	public void write(OutputStream os, InputStream is)
	{
		// Non implémentée.
	}


	/**
	 * Donne le caractère de fin-de-ligne/fin-de-message (end-of-line/end-of-message).
	 *
	 * @return le caractère eolByte.
	 */
	@Override
	public byte getEolByte()
	{
		return this.eolByte;
	}

	/**
	 * Fixe le caractère de fin-de-ligne/fin-de-message (end-of-line/end-of-message).
	 *
	 * @param eolByte le caractère à fixer.
	 */
	@Override
	public void setEolByte(int eolByte)
	{
		this.eolByte = (byte) eolByte;
	}

	/**
	 * Retourne le charset (non-utilisé actuellement).
	 * 
	 * @return le charset utilisé.
	 */
	@Override
	public String getCharset() 
	{
		return null;
	}
}

