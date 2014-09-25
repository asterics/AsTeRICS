/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Bounds.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Bounds__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Bounds__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Bounds :
	public InteractionObject
{	
public:
	Bounds(const std::shared_ptr<const Context>& spContext, TX_HANDLE hBounds);

	TX_BOUNDSTYPE GetType() const;

public:
	bool TryGetRectangularData(TX_REAL* pX, TX_REAL* pY, TX_REAL* pWidth, TX_REAL* pHeight) const;
	void SetRectangularData(TX_REAL x, TX_REAL y, TX_REAL width, TX_REAL height);
	
	bool TryGetRectangularData(TX_RECT* pData) const;
	void SetRectangularData(const TX_RECT& data);

	std::shared_ptr<InteractionObject> GetData() const;
	void SetData(const std::shared_ptr<InteractionObject>& spData);
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Bounds__HPP__)

/*********************************************************************************************************************/
