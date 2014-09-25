/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Notification.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Notification__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Notification__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

class Notification :
	public InteractionObject
{
public:
	Notification(const std::shared_ptr<const Context>& spContext, TX_HANDLE hNotification);
	              
    TX_NOTIFICATIONTYPE GetNotificationType() const;
    std::shared_ptr<InteractionObject> GetData() const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Notification__HPP__)

/*********************************************************************************************************************/
