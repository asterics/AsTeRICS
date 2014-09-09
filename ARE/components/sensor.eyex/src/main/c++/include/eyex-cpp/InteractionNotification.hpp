/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionNotification.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONNOTIFICATION__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONNOTIFICATION__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

class InteractionNotification :
	public InteractionObject
{
public:
	InteractionNotification(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hNotification);
	              
    TX_NOTIFICATIONTYPE GetNotificationType() const;
    std::shared_ptr<InteractionObject> GetData() const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONNOTIFICATION__HPP__)

/*********************************************************************************************************************/
