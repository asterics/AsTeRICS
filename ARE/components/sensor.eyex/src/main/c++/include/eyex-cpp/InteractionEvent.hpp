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
	InteractionEvent(const std::shared_ptr<const Context>& spContext, TX_HANDLE hEvent);
	   
    std::string GetInteractorId() const;        
    std::vector<std::shared_ptr<Behavior>> GetBehaviors() const;
	bool TryGetBehavior(std::shared_ptr<Behavior>* pspBehavior, TX_BEHAVIORTYPE behaviorType) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONEVENT__HPP__)

/*********************************************************************************************************************/
