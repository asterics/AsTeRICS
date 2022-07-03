/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Property.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTY__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_PROPERTY__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

template <typename TValue>
bool Property::TryGetValue(TValue* pValue) const
{
    if(PropertyValueResolver<TValue>().GetValue(this, pValue) == TX_RESULT_OK)
        return true;

    return false;
}

/*********************************************************************************************************************/

template <typename TValue>
typename PropertyValueResolver<TValue>::ValueType Property::GetValue() const
{
    TValue value;
    if(TryGetValue(&value))
        return value;

    throw APIException(TX_RESULT_INVALIDPROPERTYTYPE, "Invalid property type");
}

/*********************************************************************************************************************/
    
template <typename TValue>
void Property::SetValue(const TValue& value)
{
    TX_VALIDATE(PropertyValueResolver<TValue>().SetValue(this, value));
}

/*********************************************************************************************************************/

inline Property::Property(const std::shared_ptr<const Context>& spContext, TX_PROPERTYHANDLE hProperty)
: HandleWrapper<TX_PROPERTYHANDLE>(spContext, hProperty)
{}

/*********************************************************************************************************************/

inline TX_PROPERTYVALUETYPE Property::GetValueType() const
{
	TX_PROPERTYVALUETYPE valueType;
	TX_VALIDATE(txGetPropertyValueType(_hObject, &valueType));
	return valueType;
}

/*********************************************************************************************************************/

inline std::string Property::GetName() const
{    
    return GetString(txGetPropertyName, _hObject);
}

/*********************************************************************************************************************/

inline TX_PROPERTYFLAGS Property::GetFlags() const
{
	TX_PROPERTYFLAGS flags;
	TX_VALIDATE(txGetPropertyFlags(_hObject, &flags));
	return flags;
}

/*********************************************************************************************************************/

inline void Property::Clear()
{
	TX_VALIDATE(txClearPropertyValue(_hObject));
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTY__INL__)

/*********************************************************************************************************************/
