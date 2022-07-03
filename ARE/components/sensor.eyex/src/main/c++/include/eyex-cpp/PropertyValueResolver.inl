/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * PropertyValueResolver.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTYVALUERESOLVER__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_PROPERTYVALUERESOLVER__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

template <typename TValue>
struct PropertyValueResolver :
    public PropertyValueResolverBase<TValue>
{
    TX_RESULT GetValue(const Property* pProperty, TValue* pValue) const
    {
        return txGetPropertyValueAsInteger(pProperty->GetHandle(), (int*)pValue);
    }

    TX_RESULT SetValue(Property* pProperty, TValue value) const
    {
        return txSetPropertyValueAsInteger(pProperty->GetHandle(), (int)value);
    }
};

/*********************************************************************************************************************/

template <>
struct PropertyValueResolver<bool> :
    public PropertyValueResolverBase<bool>
{
    TX_RESULT GetValue(const Property* pProperty, bool* pValue) const
    {
        int intValue;
        auto result = txGetPropertyValueAsInteger(pProperty->GetHandle(), &intValue);
        if(result != TX_RESULT_OK)
            return result;

        *pValue = intValue != 0;
        return TX_RESULT_OK;
    }

    TX_RESULT SetValue(Property* pProperty, bool value) const
    {
        auto intValue = value ? 1 : 0;
        return txSetPropertyValueAsInteger(pProperty->GetHandle(), intValue);
    }
};

/*********************************************************************************************************************/

template <>
struct PropertyValueResolver<double> :
    public PropertyValueResolverBase<double>
{
    TX_RESULT GetValue(const Property* pProperty, double* pValue) const
    {
        return txGetPropertyValueAsReal(pProperty->GetHandle(), pValue);
    }
    
    TX_RESULT SetValue(Property* pProperty, double value) const
    {
        return txSetPropertyValueAsReal(pProperty->GetHandle(), value);
    }
};

/*********************************************************************************************************************/

template <>
struct PropertyValueResolver<float> :
    public PropertyValueResolverBase<float>
{
    TX_RESULT GetValue(const Property* pProperty, float* pValue) const
    {
        double doubleValue;
        auto result = txGetPropertyValueAsReal(pProperty->GetHandle(), &doubleValue);
        if(result != TX_RESULT_OK)
            return result;

        *pValue = (float)doubleValue;
        return TX_RESULT_OK;
    }
    
    TX_RESULT SetValue(Property* pProperty, float value) const
    {
        return txSetPropertyValueAsReal(pProperty->GetHandle(), value);
    }
};

/*********************************************************************************************************************/

template <>
struct PropertyValueResolver<std::string> :
    public PropertyValueResolverBase<std::string>
{
    TX_RESULT GetValue(const Property* pProperty, std::string* pValue) const
    {
        return Tx::Utils::GetString(pValue, txGetPropertyValueAsString, pProperty->GetHandle());
    }

    TX_RESULT SetValue(Property* pProperty, const std::string& value) const
    {
        return txSetPropertyValueAsString(pProperty->GetHandle(), value.c_str());
    }
};

/*********************************************************************************************************************/

template <typename TInteractionObject>
struct PropertyValueResolver<std::shared_ptr<TInteractionObject>> :
    public PropertyValueResolverBase<std::shared_ptr<TInteractionObject>>
{
    TX_RESULT GetValue(const Property* pProperty, std::shared_ptr<TInteractionObject>* pspValue) const
    {
        Tx::Utils::ScopedHandle hObject;
        auto result = txGetPropertyValueAsObject(pProperty->GetHandle(), &hObject);
        if(result != TX_RESULT_OK)
            return result;

        // CreateObject will detach scoped handle.
        *pspValue = pProperty->GetContext()->CreateObject(hObject);
        return TX_RESULT_OK;
    }
    
    TX_RESULT SetValue(Property* pProperty, const std::shared_ptr<TInteractionObject>& spValue) const
    {
        return txSetPropertyValueAsObject(pProperty->GetHandle(), spValue->GetHandle());
    }
};

/*********************************************************************************************************************/

template <typename TValue>
struct CompositePropertyValueResolver :
    public PropertyValueResolverBase<TValue>
{
    TX_RESULT GetValue(const Property* pProperty, TValue* pValue) const
    {
        std::shared_ptr<InteractionObject> spObject;
        if(!pProperty->TryGetValue(&spObject))
            return TX_RESULT_NOTFOUND;

        try
        {
            return GetContent(spObject, pValue);
        }
        catch(...)
        {
            return TX_RESULT_NOTFOUND;
        }

        return TX_RESULT_OK;
    }

    TX_RESULT SetValue(Property* pProperty, const TX_RECT& value) const
    {        
        try
        {
            auto spObject = pProperty->GetContext()->CreateBag();
            auto result = SetContent(spObject, value);
            if(result != TX_RESULT_OK)
                return result;
            
            pProperty->SetValue(spObject);
        }
        catch(...)
        {
            return TX_RESULT_NOTFOUND;
        }

        return TX_RESULT_OK;
    }

    virtual TX_RESULT GetContent(const std::shared_ptr<const InteractionObject>& spObject, TValue* pValue) const = 0;
    virtual TX_RESULT SetContent(const std::shared_ptr<InteractionObject>& spObject, const TValue& Value) const = 0;
};

/*********************************************************************************************************************/

template <>
struct PropertyValueResolver<TX_RECT> :
    public CompositePropertyValueResolver<TX_RECT>
{
   TX_RESULT GetContent(const std::shared_ptr<const InteractionObject>& spObject, ValueType* pValue) const
   {
        if(spObject->TryGetPropertyValue(&pValue->X, TX_LITERAL_X) &&
           spObject->TryGetPropertyValue(&pValue->Y, TX_LITERAL_Y) &&
           spObject->TryGetPropertyValue(&pValue->Width, TX_LITERAL_WIDTH) &&
           spObject->TryGetPropertyValue(&pValue->Height, TX_LITERAL_HEIGHT))
           return TX_RESULT_OK;

        return TX_RESULT_NOTFOUND;
   }

   TX_RESULT SetContent(const std::shared_ptr<InteractionObject>& spObject, const ValueType& value) const
   {
        spObject->SetPropertyValue(TX_LITERAL_X, value.X);
        spObject->SetPropertyValue(TX_LITERAL_Y, value.Y);
        spObject->SetPropertyValue(TX_LITERAL_WIDTH, value.Width);
        spObject->SetPropertyValue(TX_LITERAL_HEIGHT, value.Height);

        return TX_RESULT_OK;
   }
};

/*********************************************************************************************************************/

template <>
struct PropertyValueResolver<TX_VECTOR2> :
    public CompositePropertyValueResolver<TX_VECTOR2>
{
   TX_RESULT GetContent(const std::shared_ptr<const InteractionObject>& spObject, ValueType* pValue) const
   {
        if(spObject->TryGetPropertyValue(&pValue->X, TX_LITERAL_X) &&
           spObject->TryGetPropertyValue(&pValue->Y, TX_LITERAL_Y))
           return TX_RESULT_OK;

        return TX_RESULT_NOTFOUND;
   }

   TX_RESULT SetContent(const std::shared_ptr<InteractionObject>& spObject, const ValueType& value) const
   {
        spObject->SetPropertyValue(TX_LITERAL_X, value.X);
        spObject->SetPropertyValue(TX_LITERAL_Y, value.Y);

        return TX_RESULT_OK;
   }
};

/*********************************************************************************************************************/

template <>
struct PropertyValueResolver<TX_SIZE2> :
    public CompositePropertyValueResolver<TX_SIZE2>
{
   TX_RESULT GetContent(const std::shared_ptr<const InteractionObject>& spObject, ValueType* pValue) const
   {
        if(spObject->TryGetPropertyValue(&pValue->Width, TX_LITERAL_WIDTH) &&
           spObject->TryGetPropertyValue(&pValue->Height, TX_LITERAL_HEIGHT))
           return TX_RESULT_OK;

        return TX_RESULT_NOTFOUND;
   }

   TX_RESULT SetContent(const std::shared_ptr<InteractionObject>& spObject, const ValueType& value) const
   {
        spObject->SetPropertyValue(TX_LITERAL_WIDTH, value.Width);
        spObject->SetPropertyValue(TX_LITERAL_HEIGHT, value.Height);

        return TX_RESULT_OK;
   }
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTYVALUERESOLVER__INL__)

/*********************************************************************************************************************/
