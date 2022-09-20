/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 */

/**
 * This class contains multipart message.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 11, 2011
 *         Time: 3:22:17 PM
 */

#include "StdAfx.h"
#include "MultiPartMessage.h"
#include <algorithm>

/**
 * The class constructor.
 */
MultiPartMessage::MultiPartMessage(void)
{
	allMessageReceived=false;
	noMoreMessages=false;
	numberOfMessageParts=0;
	messageReference=0;

}

/**
 * The class destructor.
 */
MultiPartMessage::~MultiPartMessage(void)
{
	messagePart.clear();
	phoneID.clear();
}

/**
 * Adds the part of the message.
 * @param messageNumber number of the message part
 * @param numberOfMessages number of the message parts
 * @param phoneID sender phone ID
 * @param messageContent content of the message part
 * @param positioninMemory position of the part in the Modem memory
 * @param lastMessage defines if this message part should be the last
 * @return 1
 */
int MultiPartMessage::addPart(unsigned int messageNumber,unsigned int numberOfMessages,std::wstring phoneID,std::wstring messageContent,int positioninMemory,bool lastMessage)
{
	if(numberOfMessageParts!=numberOfMessages)
	{
		//warning
		numberOfMessageParts=numberOfMessages;
	}

	if(this->phoneID.compare(phoneID)!=0)
	{
		this->phoneID=phoneID;
	}

	bool found=false;
	int index=-1;

	 noMoreMessages=lastMessage;

	for(unsigned int i=0;i<messagePart.size();i++)
	{
		if(messagePart[i].numberOfPart==messageNumber)
		{
			index=i;
			found=true;
			break;
		}
	}

	if(found==true)
	{
		if(messagePart[index].message!=messageContent)
		{
			messagePart[index].message=messageContent;
		}
		if(messagePart[index].positionInMemory!=positioninMemory)
		{
			messagePart[index].positionInMemory=positioninMemory;
		}
	}
	else
	{
		MessagePart part;
		part.numberOfPart=messageNumber;
		part.message=messageContent;
		part.positionInMemory=positioninMemory;
		messagePart.push_back(part);
	}

	std::sort(messagePart.begin(),messagePart.end(),MessagePart::compare);
	
	std::vector<MessagePart>::iterator it;


	bool foundAll=true;
	int i=0;
	for ( it=messagePart.begin() ; it < messagePart.end(); it++ )
	{
		i++;
		if(it->numberOfPart!=i)
		{
			foundAll=false;
			break;
		}
	}
	
	if((foundAll==true)&&(messagePart.size()>=numberOfMessages))
	{
		allMessageReceived=true;
	}

	return 1;
}

/**
 * Returns message parts position in modem memory
 * @param position vector of positions in modem memory
 * @return 1
 */
int MultiPartMessage::getMessagePartsPossitions(std::vector<int>& position)
{
	std::sort(messagePart.begin(),messagePart.end(),MessagePart::compare);

	position.clear();

	std::vector<MessagePart>::iterator it;
	for ( it=messagePart.begin() ; it < messagePart.end(); it++ )
	{
		position.push_back(it->positionInMemory);
	}

	return 1;
}

/**
 * Returns the SMS data: content and sender ID.
 * @param SMSData contains SMS data
 * @return 1
 */
int MultiPartMessage::getMessage(SMSData& sms)
{
	std::sort(messagePart.begin(),messagePart.end(),MessagePart::compare);
	std::vector<MessagePart>::iterator it;

	std::wstring content;

	for ( it=messagePart.begin() ; it < messagePart.end(); it++ )
	{
		content=content+it->message;
	}

	sms.messageContent=content;
	sms.phoneID=phoneID;

	return 1;
}