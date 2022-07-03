/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Behavior.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Behavior__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Behavior__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Behavior :
	public InteractionObject
{
public:
    Behavior(const std::shared_ptr<const Context>& spContext, TX_HANDLE hBehavior);

    TX_BEHAVIORTYPE GetType() const;
	
public:
    void SetData(const std::shared_ptr<InteractionObject>& spData);
    std::shared_ptr<InteractionObject> GetData() const;

    void SetGazePointDataBehaviorParams(const TX_GAZEPOINTDATAPARAMS& pParams);
    bool TryGetGazePointDataBehaviorParams(TX_GAZEPOINTDATAPARAMS* pParams) const;
    bool TryGetGazePointDataEventParams(TX_GAZEPOINTDATAEVENTPARAMS* pEventParams) const;

    void SetActivatableBehaviorParams(const TX_ACTIVATABLEPARAMS& pParams);
    bool TryGetActivatableBehaviorParams(TX_ACTIVATABLEPARAMS* pParams) const;

    void SetPannableBehaviorParams(const TX_PANNABLEPARAMS& pParams);
    bool TryGetPannableBehaviorParams(TX_PANNABLEPARAMS* pParams) const;
    bool TryGetPannableEventType(TX_PANNABLEEVENTTYPE* pEventType) const;
    bool TryGetPannablePanEvent(TX_PANNABLEPANEVENTPARAMS* pEventParams) const;
    bool TryGetPannableStepEvent(TX_PANNABLESTEPEVENTPARAMS* pEventParams) const;
    bool TryGetPannableHandsFreeEvent(TX_PANNABLEHANDSFREEEVENTPARAMS* pEventParams) const;

    bool TryGetActivatableEventType(TX_ACTIVATABLEEVENTTYPE* pEventType) const;
    bool TryGetActivationFocusChangedEventParams(TX_ACTIVATIONFOCUSCHANGEDEVENTPARAMS* pEventParams) const;
	    
    void SetGazeAwareBehaviorParams(const TX_GAZEAWAREPARAMS& pParams);
    bool TryGetGazeAwareBehaviorParams(TX_GAZEAWAREPARAMS* pParams) const;
    bool TryGetGazeAwareEventParams(TX_GAZEAWAREEVENTPARAMS* pEventParams) const;

    void SetFixationDataBehaviorParams(const TX_FIXATIONDATAPARAMS& params); 
    bool TryGetFixationDataBehaviorParams(TX_FIXATIONDATAPARAMS* pParams) const;
    bool TryGetFixationDataEventParams(TX_FIXATIONDATAEVENTPARAMS* pEventParams) const;

    bool TryGetEyePositionDataEventParams(TX_EYEPOSITIONDATAEVENTPARAMS* pEventParams) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Behavior__HPP__)

/*********************************************************************************************************************/
