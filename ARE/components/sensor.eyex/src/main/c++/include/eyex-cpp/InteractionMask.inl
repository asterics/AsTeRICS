/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionMask.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONMASK__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONMASK__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline InteractionMask::InteractionMask(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hInteractionMask) 
: InteractionObject(spContext, hInteractionMask)
{ }

/*********************************************************************************************************************/

inline int InteractionMask::GetColumnCount() const
{
    int columnCount, rowCount;
    TX_VALIDATE(txGetInteractionMaskData(_hObject, &columnCount, &rowCount, nullptr, nullptr));
    return columnCount;
}

/*********************************************************************************************************************/

inline int InteractionMask::GetRowCount() const
{    
    int columnCount, rowCount;
    TX_VALIDATE(txGetInteractionMaskData(_hObject, &columnCount, &rowCount, nullptr, nullptr));
    return rowCount;
}

/*********************************************************************************************************************/

inline void InteractionMask::GetData(std::vector<TX_BYTE>& data) const
{
    int columnCount, rowCount, dataSize = 0;
    if(TX_VALIDATE(txGetInteractionMaskData(_hObject, &columnCount, &rowCount, nullptr, &dataSize)))
        return;

    data.resize(dataSize);
    TX_VALIDATE(txGetInteractionMaskData(_hObject, &columnCount, &rowCount, &data[0], &dataSize));
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONMASK__INL__)

/*********************************************************************************************************************/
