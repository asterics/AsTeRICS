/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionObject.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONOBJECT__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONOBJECT__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

template <typename THandle>
class InteractionObjectBase :
    public HandleWrapper<THandle>
{
public:
	InteractionObjectBase(const std::shared_ptr<const Context>& spContext, THandle hObject);
		
	TX_INTERACTIONOBJECTTYPE GetObjectType() const;

	bool TryGetProperty(std::shared_ptr<Property>* pspProperty, const std::string& propertyName) const;
	std::shared_ptr<Property> GetProperty(const std::string& propertyName) const;
	std::vector<std::shared_ptr<Property>> GetProperties() const;
	void CopyPropertiesTo(const std::shared_ptr<InteractionObject>& spObject) const;
	std::string FormatAsText() const;
			
public:
	template <typename TValue>
	typename PropertyValueResolver<TValue>::ValueType GetPropertyValue(const std::string& propertyName) const;

    template <typename TValue>
    bool TryGetPropertyValue(TValue* pValue, const std::string& propertyName) const;
};

/*********************************************************************************************************************/

class InteractionObject :
    public InteractionObjectBase<TX_HANDLE>
{
public:
	InteractionObject(const std::shared_ptr<const Context>& spContext, TX_HANDLE hObject);
	virtual ~InteractionObject();
		
	std::shared_ptr<Property> CreateProperty(const std::string& propertyName);
				
public:
	template <typename TValue>
	void SetPropertyValue(const std::string& propertyName, const TValue& value);
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONOBJECT__HPP__)

/*********************************************************************************************************************/
