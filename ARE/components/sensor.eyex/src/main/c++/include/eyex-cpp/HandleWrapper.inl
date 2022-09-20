/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionObject.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_HANDLEWRAPPER__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_HANDLEWRAPPER__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

template <typename THandle>
inline HandleWrapper<THandle>::HandleWrapper(const std::shared_ptr<const Context>& spContext, THandle hObject)
: _spContext(spContext), _hObject(hObject)
{}

/*********************************************************************************************************************/

template <typename THandle>
inline HandleWrapper<THandle>::~HandleWrapper()
{ }

/*********************************************************************************************************************/

template <typename THandle>
inline std::shared_ptr<const Context> HandleWrapper<THandle>::GetContext() const
{
	return _spContext;
}

/*********************************************************************************************************************/

template <typename THandle>
inline THandle HandleWrapper<THandle>::GetHandle() const
{
	return _hObject;
}

/*********************************************************************************************************************/

template <typename THandle>
template <typename THandle1, typename THandle2>
inline static std::string HandleWrapper<THandle>::GetString(TX_RESULT (*pFn)(THandle1, TX_STRING, TX_SIZE*), THandle2 handle, TX_SIZE estimatedLength)
{
    std::string str;
    TX_VALIDATE(Tx::Utils::GetString(&str, pFn, handle, estimatedLength));
    return str;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_HANDLEWRAPPER__INL__)

/*********************************************************************************************************************/
