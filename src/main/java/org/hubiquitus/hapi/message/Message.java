package org.hubiquitus.hapi.message;

/**
 * Model representing a message
 *
 * @author t.bourgeois
 */
public class Message {

    private String mFrom;
    private Object mContent;

    public Message() {
    }

    public Message(String from, Object content) {
        mFrom = from;
        mContent = content;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        this.mFrom = from;
    }

    public Object getContent() {
        return mContent;
    }

    public void setContent(Object content) {
        this.mContent = content;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mContent == null) ? 0 : mContent.hashCode());
        result = prime * result + ((mFrom == null) ? 0 : mFrom.hashCode());
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
        Message other = (Message) obj;
        if (mContent == null) {
            if (other.mContent != null)
                return false;
        } else if (!mContent.equals(other.mContent))
            return false;
        if (mFrom == null) {
            if (other.mFrom != null)
                return false;
        } else if (!mFrom.equals(other.mFrom))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Message [from=" + mFrom + ", content=" + mContent + "]";
    }

}
