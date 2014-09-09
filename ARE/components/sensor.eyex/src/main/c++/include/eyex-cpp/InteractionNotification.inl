/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionNotification.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONNOTIFICATION__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONNOTIFICATION__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

inline InteractionNotification::InteractionNotification(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hNotification) 
: InteractionObject(spContext, hNotification)
{ }

/*********************************************************************************************************************/
	              
inline TX_NOTIFICATIONTYPE InteractionNotification::GetNotificationType() const
{
    TX_NOTIFICATIONTYPE notificationType;
    TX_VALIDATE(txGetNotificationType(_hObject, &notificationType));
    return notificationType;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> InteractionNotification::GetData() const
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


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONNOTIFICATION__INL__)

/*********************************************************************************************************************/
