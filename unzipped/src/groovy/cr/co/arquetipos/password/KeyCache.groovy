package cr.co.arquetipos.password
/**
 * Simple key/password pair cache, with an attached timestamp to indicate how
 * long it was since it was evaluated.
 */

public class KeyCache {
    public String key
    public String password

    private Calendar timestamp = Calendar.getInstance()
    

    public boolean isExpired(int seconds)
    {
        def now = Calendar.getInstance()
        float diff = now.timeInMillis - timestamp.timeInMillis
        diff /= 1000
        return diff > seconds
    }

    public void setPassword(String newPassword)
    {
        this.password = newPassword
        touch()
    }

    public void touch()
    {
        timestamp = Calendar.getInstance()
    }

}