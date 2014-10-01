/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionObject.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONOBJECT__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONOBJECT__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

template <typename THandle>
inline InteractionObjectBase<THandle>::InteractionObjectBase(const std::shared_ptr<const Context>& spContext, THandle hObject)
: HandleWrapper(spContext, hObject)
{ }

/*********************************************************************************************************************/

template <typename THandle>
inline TX_INTERACTIONOBJECTTYPE InteractionObjectBase<THandle>::GetObjectType() const
{
	TX_INTERACTIONOBJECTTYPE objectType;
	TX_VALIDATE(txGetObjectType(_hObject, &objectType));
	return objectType;
}

/*********************************************************************************************************************/

template <typename THandle>
inline bool InteractionObjectBase<THandle>::TryGetProperty(std::shared_ptr<Property>* pspProperty, const std::string& propertyName) const
{
	TX_PROPERTYHANDLE hProperty;
	auto result = txGetProperty(_hObject, &hProperty, propertyName.c_str());
	if(result == TX_RESULT_NOTFOUND)
		return false;

	*pspProperty = _spContext->CreateProperty(hProperty);
	return true;
}

/*********************************************************************************************************************/

template <typename THandle>
inline std::shared_ptr<Property> InteractionObjectBase<THandle>::GetProperty(const std::string& propertyName) const
{
	std::shared_ptr<Property> spProperty;
	if(TryGetProperty(&spProperty, propertyName))
		return spProperty;
	
	throw APIException(TX_RESULT_NOTFOUND, "Property not found");
}

/*********************************************************************************************************************/

template <typename THandle>
inline std::vector<std::shared_ptr<Property>> InteractionObjectBase<THandle>::GetProperties() const 
{
	std::vector<TX_PROPERTYHANDLE> propertyHandles;
    TX_VALIDATE(Tx::Utils::GetBufferData(propertyHandles, txGetProperties, _hObject));
    
	std::vector<std::shared_ptr<Property>> properties;	
	for(auto& hProperty : propertyHandles)
	{
		auto spProperty = _spContext->CreateProperty(hProperty);
		properties.push_back(spProperty);
	}

	return properties;
}

/*********************************************************************************************************************/

template <typename THandle>
inline void InteractionObjectBase<THandle>::CopyPropertiesTo(const std::shared_ptr<InteractionObject>& spObject) const
{
	TX_VALIDATE(txCopyProperties(_hObject, spObject->GetHandle()));
}

/*********************************************************************************************************************/

template <typename THandle>
inline std::string InteractionObjectBase<THandle>::FormatAsText() const
{
    return GetString(txFormatObjectAsText, _hObject, 512);
}

/*********************************************************************************************************************/

template <typename THandle>
template <typename TValue>
inline typename PropertyValueResolver<TValue>::ValueType InteractionObjectBase<THandle>::GetPropertyValue(const std::string& propertyName) const
{
	auto spProperty = GetProperty(propertyName);
	return spProperty->GetValue<TValue>();
}

/*********************************************************************************************************************/

template <typename THandle>
template <typename TValue>
inline bool InteractionObjectBase<THandle>::TryGetPropertyValue(TValue* pValue, const std::string& propertyName) const
{
    std::shared_ptr<Property> spProperty;
	if(!TryGetProperty(&spProperty, propertyName))
		return false;

    if(!spProperty->TryGetValue(pValue))
        return false;

    return true;
}

/*********************************************************************************************************************/


/*********************************************************************************************************************/

inline InteractionObject::InteractionObject(const std::shared_ptr<const Context>& spContext, TX_HANDLE hObject)
: InteractionObjectBase<TX_HANDLE>(spContext, hObject)
{ }

/*********************************************************************************************************************/

inline InteractionObject::~InteractionObject()
{
	txReleaseObject(&_hObject);
}

/*********************************************************************************************************************/

inline std::shared_ptr<Property> InteractionObject::CreateProperty(const std::string& propertyName)
{
	TX_PROPERTYHANDLE hProperty;
	TX_VALIDATE(txCreateProperty(_hObject, &hProperty, propertyName.c_str()));
	auto spProperty = _spContext->CreateProperty(hProperty);
	return spProperty;
}

/*********************************************************************************************************************/

template <typename TValue>
inline void InteractionObject::SetPropertyValue(const std::string& propertyName, const TValue& value)
{
	std::shared_ptr<Property> spProperty;
	if(!TryGetProperty(&spProperty, propertyName))
		spProperty = CreateProperty(propertyName);

	spProperty->SetValue(value);
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONOBJECT__INL__)

/*********************************************************************************************************************/
