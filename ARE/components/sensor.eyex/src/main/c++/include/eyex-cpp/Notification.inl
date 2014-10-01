/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Notification.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Notification__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Notification__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

inline Notification::Notification(const std::shared_ptr<const Context>& spContext, TX_HANDLE hNotification) 
: InteractionObject(spContext, hNotification)
{ }

/*********************************************************************************************************************/
	              
inline TX_NOTIFICATIONTYPE Notification::GetNotificationType() const
{
    TX_NOTIFICATIONTYPE notificationType;
    TX_VALIDATE(txGetNotificationType(_hObject, &notificationType));
    return notificationType;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> Notification::GetData() const
{
    Tx::Utils::ScopedHandle hData;
    if(!TX_VALIDATE(txGetNotificationData(_hObject, &hData), TX_RESULT_NOTFOUND))
        return nullptr;

    auto spData = _spContext->CreateObject(hData);
	return spData;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Notification__INL__)

/*********************************************************************************************************************/
