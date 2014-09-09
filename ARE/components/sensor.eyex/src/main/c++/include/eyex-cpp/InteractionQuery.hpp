/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionQuery.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONQUERY__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONQUERY__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

class InteractionQuery :
	public InteractionObject
{
public:	
	InteractionQuery(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hQuery);
	   
    std::shared_ptr<InteractionBounds> GetBounds() const;
    std::vector<std::string> GetWindowIds() const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONQUERY__HPP__)

/*********************************************************************************************************************/
