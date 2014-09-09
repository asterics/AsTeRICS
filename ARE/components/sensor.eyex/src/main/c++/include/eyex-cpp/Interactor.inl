/*********************************************************************************************************************
* Copyright 2013-2014 Tobii Technology AB. All rights reserved.
* Interactor.inl
*********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

    /*********************************************************************************************************************/

    inline Interactor::Interactor(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hInteractor)
    : InteractionObject(spContext, hInteractor)
{}

/*********************************************************************************************************************/

inline bool Interactor::GetEnabled() const
{
    int isEnabled;
    TX_VALIDATE(txGetInteractorEnabled(_hObject, &isEnabled));
    return isEnabled != 0;
}

/*********************************************************************************************************************/

inline void Interactor::SetEnabled(bool enabled)
{
    TX_VALIDATE(txSetInteractorEnabled(_hObject, enabled ? 1 : 0));
}

/*********************************************************************************************************************/

inline bool Interactor::GetDeleted() const
{
    int isDeleted;
    TX_VALIDATE(txGetInteractorDeleted(_hObject, &isDeleted));
    return isDeleted != 0;
}

/*********************************************************************************************************************/

inline void Interactor::SetDeleted(bool deleted)
{
    TX_VALIDATE(txSetInteractorDeleted(_hObject, deleted ? 1 : 0));
}

/*********************************************************************************************************************/

inline std::string Interactor::GetId() const
{
    return GetString(txGetInteractorId, _hObject);
}

/*********************************************************************************************************************/

inline std::string Interactor::GetParentId() const
{    
    return GetString(txGetInteractorParentId, _hObject);
}

/*********************************************************************************************************************/

inline std::string Interactor::GetWindowId() const
{
    return GetString(txGetInteractorWindowId, _hObject);
}

/*********************************************************************************************************************/

inline double Interactor::GetZ() const
{
    double z;
    TX_VALIDATE(txGetInteractorZ(_hObject, &z));
    return z;
}

/*********************************************************************************************************************/

inline void Interactor::SetZ(double z)
{
    TX_VALIDATE(txSetInteractorZ(_hObject, z));
}

/*********************************************************************************************************************/

inline void Interactor::SetGazePointDataBehavior(const TX_GAZEPOINTDATAPARAMS& params)
{

    TX_VALIDATE(txSetGazePointDataBehavior(_hObject, &params));
}

/*********************************************************************************************************************/

inline void Interactor::SetActivatableBehavior(const TX_ACTIVATABLEPARAMS& params)
{    
    TX_VALIDATE(txSetActivatableBehavior(_hObject, &params));
}

/*********************************************************************************************************************/

inline void Interactor::SetPannableBehavior(const TX_PANNABLEPARAMS& params)
{
    TX_VALIDATE(txSetPannableBehavior(_hObject, &params));
}

/*********************************************************************************************************************/

inline void Interactor::SetGazeAwareBehavior(const TX_GAZEAWAREPARAMS& params)
{
    TX_VALIDATE(txSetGazeAwareBehavior(_hObject, &params));
}  

/*********************************************************************************************************************/

inline void Interactor::SetFixationDataBehaviorParams(const TX_FIXATIONDATAPARAMS& params)
{    
    TX_VALIDATE(txSetFixationDataBehavior(_hObject, &params));
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionBounds> Interactor::GetBounds() const
{
    TX_HANDLE hBounds;
    if (!TX_VALIDATE(txGetInteractorBounds(_hObject, &hBounds), TX_RESULT_NOTFOUND))
        return nullptr;

    return _spContext->CreateObject<InteractionBounds>(hBounds);
}

/*********************************************************************************************************************/

inline std::vector<std::shared_ptr<InteractionBehavior>> Interactor::GetBehaviors() const
{
    std::vector<Tx::Utils::ScopedHandle> behaviorHandles;
    TX_VALIDATE(Tx::Utils::GetBufferData(behaviorHandles, txGetInteractorBehaviors, _hObject));
        
    std::vector<std::shared_ptr<InteractionBehavior>> behaviors;   
    for(auto& hBehavior : behaviorHandles)
    {
        auto spBehavior = _spContext->CreateObject<InteractionBehavior>(hBehavior);
        behaviors.push_back(spBehavior);
    }

    return behaviors;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionBounds> Interactor::CreateBounds(TX_INTERACTIONBOUNDSTYPE boundsType)
{
    Tx::Utils::ScopedHandle hBounds;
    TX_VALIDATE(txCreateInteractorBounds(_hObject, &hBounds, boundsType));
    auto spBounds = _spContext->CreateObject<InteractionBounds>(hBounds);
    return spBounds;
}

/*********************************************************************************************************************/

inline void Interactor::DeleteBounds()
{
    TX_VALIDATE(txDeleteInteractorBounds(_hObject));
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionBehavior> Interactor::CreateBehavior(TX_INTERACTIONBEHAVIORTYPE behaviorType)
{
    Tx::Utils::ScopedHandle hBehavior;
    TX_VALIDATE(txCreateInteractorBehavior(_hObject, &hBehavior, behaviorType));
    auto spBehavior = _spContext->CreateObject<InteractionBehavior>(hBehavior);
    return spBehavior;
}

/*********************************************************************************************************************/

inline void Interactor::DeleteBehavior(TX_INTERACTIONBEHAVIORTYPE behaviorType)
{
    TX_VALIDATE(txRemoveInteractorBehavior(_hObject, behaviorType));
}

/*********************************************************************************************************************/

inline bool Interactor::TryGetBehavior(std::shared_ptr<InteractionBehavior> *pspBehavior, TX_INTERACTIONBEHAVIORTYPE behaviorType) const
{
    Tx::Utils::ScopedHandle hBehavior;
    if (!TX_VALIDATE(txGetInteractorBehavior(_hObject, &hBehavior, behaviorType), TX_RESULT_NOTFOUND))
        return false;

    *pspBehavior = _spContext->CreateObject<InteractionBehavior>(hBehavior);
    return true;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionMask> Interactor::CreateMask(TX_MASKTYPE maskType, int columnCount, int rowCount, const TX_BYTE* pData)
{
    Tx::Utils::ScopedHandle hMask;
    TX_VALIDATE(txCreateInteractorMask(_hObject, &hMask, maskType, columnCount, rowCount, pData));
    auto spMask = _spContext->CreateObject<InteractionMask>(hMask);
    return spMask;
}

/*********************************************************************************************************************/

inline void Interactor::RemoveMask()
{
    TX_VALIDATE(txRemoveInteractorMask(_hObject));
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionMask> Interactor::GetMask() const
{
    Tx::Utils::ScopedHandle hMask;
    TX_VALIDATE(txGetInteractorMask(_hObject, &hMask));
    auto spMask = _spContext->CreateObject<InteractionMask>(hMask);
    return spMask;
}

/*********************************************************************************************************************/

inline void Interactor::SetMaskBounds(const TX_RECT& bounds)
{
    TX_VALIDATE(txSetInteractorMaskBounds(_hObject, &bounds));
}

/*********************************************************************************************************************/

inline void Interactor::ClearMaskBounds() 
{
    TX_VALIDATE(txClearInteractorMaskBounds(_hObject));
}

/*********************************************************************************************************************/

inline bool Interactor::TryGetMaskBounds(TX_RECT* pBounds) const
{    
    if(!TX_VALIDATE(txGetInteractorMaskBounds(_hObject, pBounds), TX_RESULT_NOTFOUND))
        return false;

    return true;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

    /*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__INL__)

    /*********************************************************************************************************************/
