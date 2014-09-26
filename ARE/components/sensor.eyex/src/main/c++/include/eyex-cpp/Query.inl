/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Query.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Query__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Query__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline Query::Query(const std::shared_ptr<const Context>& spContext, TX_HANDLE hQuery)
: InteractionObject(spContext, hQuery)
{}

/*********************************************************************************************************************/

inline std::shared_ptr<Bounds> Query::GetBounds() const
{
	Tx::Utils::ScopedHandle hBounds;
	TX_VALIDATE(txGetQueryBounds(_hObject, &hBounds));
	auto spBounds = _spContext->CreateObject<Bounds>(hBounds);
	return spBounds;
}

/*********************************************************************************************************************/

inline std::vector<std::string> Query::GetWindowIds() const
{
    TX_SIZE windowIdCount;
    TX_VALIDATE(txGetQueryWindowIdCount(_hObject, &windowIdCount));

    std::vector<std::string> windowIds;
    for (int i = 0; i < windowIdCount; i++)
    {
        std::string windowId;
        TX_VALIDATE(Tx::Utils::GetString(&windowId, [i, this](TX_CHAR* pBuf, TX_SIZE* pSize) 
        {
            return txGetQueryWindowId(_hObject, i, pBuf, pSize);
        }));

        windowIds.push_back(windowId);
    }
    return windowIds;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Query__INL__)

/*********************************************************************************************************************/
