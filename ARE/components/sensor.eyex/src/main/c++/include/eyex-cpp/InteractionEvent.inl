/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionEvent.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONEVENT__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONEVENT__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline InteractionEvent::InteractionEvent(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hEvent)
: InteractionObject(spContext, hEvent)
{}

/*********************************************************************************************************************/
	   
inline std::string InteractionEvent::GetInteractorId() const        
{	
    return GetString(txGetEventInteractorId, _hObject);
}

/*********************************************************************************************************************/

inline std::vector<std::shared_ptr<InteractionBehavior>> InteractionEvent::GetBehaviors() const
{	   
	std::vector<Tx::Utils::ScopedHandle> behaviorHandles;
    TX_VALIDATE(Tx::Utils::GetBufferData(behaviorHandles, txGetEventBehaviors, _hObject));
	
    std::vector<std::shared_ptr<InteractionBehavior>> behaviors;
	for(auto& hBehavior : behaviorHandles)
	{
		auto spBehavior = _spContext->CreateObject<InteractionBehavior>(hBehavior);
		behaviors.push_back(spBehavior);
	}

	return behaviors;
}

/*********************************************************************************************************************/

inline bool InteractionEvent::TryGetBehavior(std::shared_ptr<InteractionBehavior>* pspBehavior, TX_INTERACTIONBEHAVIORTYPE behaviorType) const
{
	Tx::Utils::ScopedHandle hBehavior;
	if(!TX_VALIDATE(txGetEventBehavior(_hObject, &hBehavior, behaviorType), TX_RESULT_NOTFOUND))
		return false;

	*pspBehavior = _spContext->CreateObject<InteractionBehavior>(hBehavior);
	return true;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONEVENT__INL__)

/*********************************************************************************************************************/
