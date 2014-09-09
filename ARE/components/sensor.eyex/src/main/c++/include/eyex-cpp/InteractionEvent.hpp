/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionEvent.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONEVENT__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONEVENT__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

class InteractionEvent :
	public InteractionObject
{
public:
	InteractionEvent(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hEvent);
	   
    std::string GetInteractorId() const;        
    std::vector<std::shared_ptr<InteractionBehavior>> GetBehaviors() const;
	bool TryGetBehavior(std::shared_ptr<InteractionBehavior>* pspBehavior, TX_INTERACTIONBEHAVIORTYPE behaviorType) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONEVENT__HPP__)

/*********************************************************************************************************************/
