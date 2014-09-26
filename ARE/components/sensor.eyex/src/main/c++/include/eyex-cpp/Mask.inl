/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Mask.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_MASK__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_MASK__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline Mask::Mask(const std::shared_ptr<const Context>& spContext, TX_HANDLE hMask) 
: InteractionObject(spContext, hMask)
{ }

/*********************************************************************************************************************/

inline int Mask::GetColumnCount() const
{
    int columnCount, rowCount;
    TX_VALIDATE(txGetMaskData(_hObject, &columnCount, &rowCount, nullptr, nullptr));
    return columnCount;
}

/*********************************************************************************************************************/

inline int Mask::GetRowCount() const
{    
    int columnCount, rowCount;
    TX_VALIDATE(txGetMaskData(_hObject, &columnCount, &rowCount, nullptr, nullptr));
    return rowCount;
}

/*********************************************************************************************************************/

inline void Mask::GetData(std::vector<TX_BYTE>& data) const
{
    int columnCount, rowCount, dataSize = 0;
    if(TX_VALIDATE(txGetMaskData(_hObject, &columnCount, &rowCount, nullptr, &dataSize)))
        return;

    data.resize(dataSize);
    TX_VALIDATE(txGetMaskData(_hObject, &columnCount, &rowCount, &data[0], &dataSize));
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_MASK__INL__)

/*********************************************************************************************************************/
