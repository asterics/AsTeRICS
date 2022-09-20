/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Bounds.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Bounds__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Bounds__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline Bounds::Bounds(const std::shared_ptr<const Context>& spContext, TX_HANDLE hBounds)
: InteractionObject(spContext, hBounds)
{}

/*********************************************************************************************************************/

inline TX_BOUNDSTYPE Bounds::GetType() const
{
	TX_BOUNDSTYPE boundsType;
	TX_VALIDATE(txGetBoundsType(_hObject, &boundsType));
	return boundsType;
}

/*********************************************************************************************************************/


inline bool Bounds::TryGetRectangularData(TX_REAL* pX, TX_REAL* pY, TX_REAL* pWidth, TX_REAL* pHeight) const
{
	return txGetRectangularBoundsData(_hObject, pX, pY, pWidth, pHeight) == TX_RESULT_OK;
}

/*********************************************************************************************************************/

inline void Bounds::SetRectangularData(TX_REAL x, TX_REAL y, TX_REAL width, TX_REAL height)
{	
	txSetRectangularBoundsData(_hObject, x, y, width, height);
}

/*********************************************************************************************************************/

inline bool Bounds::TryGetRectangularData(TX_RECT* pData) const
{
	return TryGetRectangularData(&pData->X, &pData->Y, &pData->Width, &pData->Height);
}

/*********************************************************************************************************************/

inline void Bounds::SetRectangularData(const TX_RECT& data)
{
	SetRectangularData(data.X, data.Y, data.Width, data.Height);
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> Bounds::GetData() const
{		
	auto spProperty = GetProperty(TX_LITERAL_DATA);

	std::shared_ptr<InteractionObject> spData;
	if(spProperty->TryGetValue(&spData))
		return spData;

	return nullptr;
}

/*********************************************************************************************************************/

inline void Bounds::SetData(const std::shared_ptr<InteractionObject>& spData)
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);
	spProperty->SetValue(spData);
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Bounds__INL__)

/*********************************************************************************************************************/
