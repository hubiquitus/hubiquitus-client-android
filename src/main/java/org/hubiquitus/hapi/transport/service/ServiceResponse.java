package org.hubiquitus.hapi.transport.service;

/**
 * Service response class
 *
 * @author t.bourgeois
 */
public class ServiceResponse {

    private int mStatus;
    private String mText;

    public ServiceResponse() {
    }

    /**
     * @param status the status of the response
     * @param text   response mText
     */
    public ServiceResponse(int status, String text) {
        this.mStatus = status;
        this.mText = text;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    /**
     * @return the text content of the response
     */
    public String getText() {
        return mText;
    }

    /**
     * @param text the text content of the response
     */
    public void setText(String text) {
        this.mText = text;
    }

    @Override
    public String toString() {
        return "ServiceResponse [status=" + mStatus + ", text=" + mText + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mStatus;
        result = prime * result + ((mText == null) ? 0 : mText.hashCode());
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
        if (mStatus != other.mStatus)
            return false;
        if (mText == null) {
            if (other.mText != null)
                return false;
        } else if (!mText.equals(other.mText))
            return false;
        return true;
    }

}
