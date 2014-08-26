/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionBounds.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONBOUNDS__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONBOUNDS__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline InteractionBounds::InteractionBounds(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hBounds)
: InteractionObject(spContext, hBounds)
{}

/*********************************************************************************************************************/

inline TX_INTERACTIONBOUNDSTYPE InteractionBounds::GetType() const
{
	TX_INTERACTIONBOUNDSTYPE boundsType;
	TX_VALIDATE(txGetInteractionBoundsType(_hObject, &boundsType));
	return boundsType;
}

/*********************************************************************************************************************/


inline bool InteractionBounds::TryGetRectangularData(TX_REAL* pX, TX_REAL* pY, TX_REAL* pWidth, TX_REAL* pHeight) const
{
	return txGetRectangularBoundsData(_hObject, pX, pY, pWidth, pHeight) == TX_RESULT_OK;
}

/*********************************************************************************************************************/

inline void InteractionBounds::SetRectangularData(TX_REAL x, TX_REAL y, TX_REAL width, TX_REAL height)
{	
	txSetRectangularBoundsData(_hObject, x, y, width, height);
}

/*********************************************************************************************************************/

inline bool InteractionBounds::TryGetRectangularData(TX_RECT* pData) const
{
	return TryGetRectangularData(&pData->X, &pData->Y, &pData->Width, &pData->Height);
}

/*********************************************************************************************************************/

inline void InteractionBounds::SetRectangularData(const TX_RECT& data)
{
	SetRectangularData(data.X, data.Y, data.Width, data.Height);
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> InteractionBounds::GetData() const
{		
	auto spProperty = GetProperty(TX_LITERAL_DATA);

	std::shared_ptr<InteractionObject> spData;
	if(spProperty->TryGetValue(&spData))
		return spData;

	return nullptr;
}

/*********************************************************************************************************************/

inline void InteractionBounds::SetData(const std::shared_ptr<InteractionObject>& spData)
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);
	spProperty->SetValue(spData);
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONBOUNDS__INL__)

/*********************************************************************************************************************/
