package de.uniks.pmws2021.chat.model;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class User
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_IP = "ip";
   public static final String PROPERTY_STATUS = "status";
   public static final String PROPERTY_CHAT = "chat";
   private String name;
   private String ip;
   private Boolean status;
   private Chat chat;
   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public User setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public String getIp()
   {
      return this.ip;
   }

   public User setIp(String value)
   {
      if (Objects.equals(value, this.ip))
      {
         return this;
      }

      final String oldValue = this.ip;
      this.ip = value;
      this.firePropertyChange(PROPERTY_IP, oldValue, value);
      return this;
   }

   public Boolean getStatus()
   {
      return this.status;
   }

   public User setStatus(Boolean value)
   {
      if (Objects.equals(value, this.status))
      {
         return this;
      }

      final Boolean oldValue = this.status;
      this.status = value;
      this.firePropertyChange(PROPERTY_STATUS, oldValue, value);
      return this;
   }

   public Chat getChat()
   {
      return this.chat;
   }

   public User setChat(Chat value)
   {
      if (this.chat == value)
      {
         return this;
      }

      final Chat oldValue = this.chat;
      if (this.chat != null)
      {
         this.chat = null;
         oldValue.withoutAvailableUser(this);
      }
      this.chat = value;
      if (value != null)
      {
         value.withAvailableUser(this);
      }
      this.firePropertyChange(PROPERTY_CHAT, oldValue, value);
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getIp());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setChat(null);
   }
}
