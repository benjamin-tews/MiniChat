package de.uniks.pmws2021.chat.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;

public class GenModel implements ClassModelDecorator
{

	class User {
		String name;
		String ip;
		Boolean status;
		// do awesome sh1t! ...
		@Link("availableUser")
		Chat chat;
	}

	class Chat {
		String currentUsername;
		// ... and some magic here :heart:
		@Link("chat")
		List<User> availableUser;
	}

	@Override
	public void decorate(ClassModelManager mm)
	{
		// this::one is fucking awesome!!!
		mm.haveNestedClasses(GenModel.class);
	}

}
