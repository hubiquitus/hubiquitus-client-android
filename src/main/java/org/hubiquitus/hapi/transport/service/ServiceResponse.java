package org.hubiquitus.hapi.transport.service;

/**
 * Service response class
 *
 * @author t.bourgeois
 */
public class ServiceResponse {

    /**
     * The status of the response
     */
    private int status;
    /**
     * The text of the response
     */
    private String text;

    /**
     * Default constructor
     */
    public ServiceResponse() {
    }

    /**
     * Constructor
     *
     * @param status the status of the response
     * @param text
     */
    public ServiceResponse(int status, String text) {
        this.status = status;
        this.text = text;
    }

    /**
     * Getter for status
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * Setter for the status
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Getter for the text
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Setter for the text
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ServiceResponse [status=" + status + ", text=" + text + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + status;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceResponse other = (ServiceResponse) obj;
        if (status != other.status)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }

}
