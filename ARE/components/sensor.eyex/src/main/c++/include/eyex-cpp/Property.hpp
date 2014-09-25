/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Property.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTY__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_PROPERTY__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Property :
	public HandleWrapper<TX_PROPERTYHANDLE>	
{
public:
	Property(const std::shared_ptr<const Context>& spContext, TX_PROPERTYHANDLE hProperty);

	TX_PROPERTYVALUETYPE GetValueType() const;
	std::string GetName() const;
	TX_PROPERTYFLAGS GetFlags() const;

	void Clear();

	template <typename TValue>
	bool TryGetValue(TValue* pValue) const;

	template <typename TValue>
	typename PropertyValueResolver<TValue>::ValueType GetValue() const;

    template <typename TValue>
    void SetValue(const TValue& value);
};

/*********************************************************************************************************************/

TX_NAMESPACE_END
	
/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTY__HPP__)

/*********************************************************************************************************************/
