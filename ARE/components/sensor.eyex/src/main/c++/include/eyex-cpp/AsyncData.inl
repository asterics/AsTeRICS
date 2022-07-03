/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * AsyncData.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ASYNCDATA__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_ASYNC__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline AsyncData::AsyncData(const std::shared_ptr<const Context>& spContext, TX_CONSTHANDLE hAsyncData)
: InteractionObjectBase(spContext, hAsyncData)
{ }

/*********************************************************************************************************************/

inline bool AsyncData::TryGetResultCode(TX_RESULT* pResultCode) const
{
	return TX_VALIDATE(txGetAsyncDataResultCode(_hObject, pResultCode), TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> AsyncData::GetData() const
{
	Tx::Utils::ScopedHandle hData;
	if(!TX_VALIDATE(txGetAsyncDataContent(_hObject, &hData), TX_RESULT_NOTFOUND))
		return nullptr;

	auto spData = _spContext->CreateObject(hData);	
	return spData;
}

/*********************************************************************************************************************/

template <typename TInteractionObject>
std::shared_ptr<TInteractionObject> AsyncData::GetDataAs() const
{
	return std::dynamic_pointer_cast<TInteractionObject>(GetData());
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ASYNCDATA__INL__)

/*********************************************************************************************************************/
