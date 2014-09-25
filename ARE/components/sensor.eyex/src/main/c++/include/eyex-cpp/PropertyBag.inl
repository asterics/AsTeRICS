/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * PropertyBag.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTYBAG__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_PROPERTYBAG__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline PropertyBag::PropertyBag(const std::shared_ptr<const Context>& spContext, TX_HANDLE hBag)
: InteractionObject(spContext, hBag)
{}

/*********************************************************************************************************************/

inline TX_PROPERTYBAGTYPE PropertyBag::GetType() const
{
	TX_PROPERTYBAGTYPE bagType;
	TX_VALIDATE(txGetPropertyBagType(_hObject, &bagType));
	return bagType;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_PROPERTYBAG__INL__)

/*********************************************************************************************************************/
