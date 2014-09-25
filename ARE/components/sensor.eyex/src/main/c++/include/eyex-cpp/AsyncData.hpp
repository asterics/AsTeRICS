/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * CommandResult.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ASYNCDATA__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_ASYNCDATA__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

class AsyncData :
	public InteractionObjectBase<TX_CONSTHANDLE>
{
public:	
	AsyncData(const std::shared_ptr<const Context>& spContext, TX_CONSTHANDLE hAsyncData);
	   
    bool TryGetResultCode(TX_RESULT* pResultCode) const;
    std::shared_ptr<InteractionObject> GetData() const;

	template <typename TInteractionObject>
	std::shared_ptr<TInteractionObject> GetDataAs() const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ASYNCDATA__HPP__)

/*********************************************************************************************************************/
