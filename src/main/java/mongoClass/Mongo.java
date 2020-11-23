/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mongoClass;

import java.util.Date;
import javax.persistence.Id;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

/**
 *
 * @author Frederik
 */
public class Mongo {

    private ObjectId id;
    @BsonProperty(value = "expireAt")
    Date expireAt;
    @BsonProperty(value = "ip_address")
    String ip_address;
    @BsonProperty(value = "username")
    String username;
    @BsonProperty(value = "tries")
    int tries;

    public Mongo() {
    }

    public Mongo(Date expireAt, String ip_address, String username, int tries) {
        this.expireAt = expireAt;
        this.ip_address = ip_address;
        this.username = username;
        this.tries = tries;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public ObjectId getId() {
        return id;
    }
    
    public Mongo setId(ObjectId id) {
        this.id = id;
        return this;
    }
    

    public Document pojoToDoc() {
        Document logMessage = new Document();
        logMessage.append("expireAt", new Date());
        logMessage.append("ip_address", this.ip_address);
        logMessage.append("username", this.username);
        logMessage.append("tries", 1);

        return logMessage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Mongo{");
        sb.append("id=").append(id);
        sb.append(", expireAt=").append(new Date());
        sb.append(", ip_address=").append(this.ip_address);
        sb.append(", username=").append(this.username);
        sb.append(", tries=").append(1);
        sb.append('}');
        return sb.toString();
    }

}
