/*********************************************************************************************************************
* Copyright 2013-2014 Tobii Technology AB. All rights reserved.
* StateBag.inl
*********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_STATEBAG__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_STATEBAG__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline StateBag::StateBag(const std::shared_ptr<const Context>& spContext, TX_HANDLE hBag)
    : InteractionObject(spContext, hBag)
{}

/*********************************************************************************************************************/

inline std::string StateBag::GetStatePath() const
{
    return GetString(txGetStateBagPath, _hObject);
}

/*********************************************************************************************************************/

template <typename TValue>
inline bool StateBag::TryGetStateValue(TValue* pValue, const std::string& valuePath) const
{    
    std::shared_ptr<Property> spProperty;
    if(!TryGetPropertyForStateValue(&spProperty, valuePath, false))
        return false;

    return spProperty->TryGetValue(pValue);
}

/*********************************************************************************************************************/

template <typename TValue>
inline void StateBag::SetStateValue(const std::string& valuePath, const TValue& value)
{
    std::shared_ptr<Property> spProperty;
    TryGetPropertyForStateValue(&spProperty, valuePath, true);
    spProperty->SetValue(value);
}

/*********************************************************************************************************************/

inline void StateBag::SetAsync(AsyncDataHandler fnCompletion)	
{
    auto spThis = shared_from_this();

    auto callback = [&, spThis, fnCompletion](TX_CONSTHANDLE hAsyncData)
    {                
		GetContext()->InvokeAsyncDataHandler(hAsyncData, fnCompletion);
    };
    
    TX_VALIDATE(Tx::SetStateAsync(GetHandle(), callback));    
}

/*********************************************************************************************************************/

inline bool StateBag::TryGetPropertyForStateValue(std::shared_ptr<Property>* pspProperty, const std::string& valuePath, bool createIfNotExists) const
{
    TX_PROPERTYHANDLE hProperty;
    if (!TX_VALIDATE(txGetPropertyForStateValue(_hObject, &hProperty, valuePath.c_str(), createIfNotExists ? TX_TRUE : TX_FALSE), TX_RESULT_NOTFOUND))
        return false;

    *pspProperty = _spContext->CreateProperty(hProperty);
    return true;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_STATEBAG__INL__)

/*********************************************************************************************************************/
