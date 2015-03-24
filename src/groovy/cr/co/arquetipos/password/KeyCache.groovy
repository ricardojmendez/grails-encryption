package cr.co.arquetipos.password

/**
 * Simple key/password pair cache, with an attached timestamp to indicate how
 * long it was since it was evaluated.
 */
class KeyCache {
    String key
    String password

    private Calendar timestamp = Calendar.instance

    boolean isExpired(int seconds)
    {
        float diff = System.currentTimeMillis() - timestamp.timeInMillis
        diff /= 1000
        return diff > seconds
    }

    void setPassword(String newPassword)
    {
        this.password = newPassword
        touch()
    }

    void touch()
    {
        timestamp = Calendar.instance
    }
}