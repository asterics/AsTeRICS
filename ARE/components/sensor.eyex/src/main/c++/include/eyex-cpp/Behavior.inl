/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Behavior.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Behavior__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Behavior__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline Behavior::Behavior(const std::shared_ptr<const Context>& spContext, TX_HANDLE hBehavior)
: InteractionObject(spContext, hBehavior)
{}

/*********************************************************************************************************************/

inline TX_BEHAVIORTYPE Behavior::GetType() const
{
	TX_BEHAVIORTYPE behaviorType;
	TX_VALIDATE(txGetBehaviorType(_hObject, &behaviorType));
	return behaviorType;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> Behavior::GetData() const
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);

	std::shared_ptr<InteractionObject> spData;
	if(spProperty->TryGetValue(&spData))
		return spData;

	return nullptr;
}

/*********************************************************************************************************************/
	
inline void Behavior::SetData(const std::shared_ptr<InteractionObject>& spData)
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);
	spProperty->SetValue(spData);
}

/*********************************************************************************************************************/

inline void Behavior::SetGazePointDataBehaviorParams(const TX_GAZEPOINTDATAPARAMS& params)
{
    TX_VALIDATE(txSetGazePointDataBehaviorParams(_hObject, &params));
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetGazePointDataBehaviorParams(TX_GAZEPOINTDATAPARAMS* pParams) const
{
    return TX_VALIDATE(txGetGazePointDataBehaviorParams(_hObject, pParams), TX_RESULT_INVALIDBEHAVIORTYPE);
}

/*********************************************************************************************************************/

inline void Behavior::SetActivatableBehaviorParams(const TX_ACTIVATABLEPARAMS& params)
{
    TX_VALIDATE(txSetActivatableBehaviorParams(_hObject, &params));
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetActivatableBehaviorParams(TX_ACTIVATABLEPARAMS* pParams) const
{
    return TX_VALIDATE(txGetActivatableBehaviorParams(_hObject, pParams), TX_RESULT_INVALIDBEHAVIORTYPE);
}

/*********************************************************************************************************************/

inline void Behavior::SetPannableBehaviorParams(const TX_PANNABLEPARAMS& params)
{
    TX_VALIDATE(txSetPannableBehaviorParams(_hObject, &params));
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetPannableBehaviorParams(TX_PANNABLEPARAMS* pParams) const
{
    return TX_VALIDATE(txGetPannableBehaviorParams(_hObject, pParams), TX_RESULT_INVALIDBEHAVIORTYPE);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetPannableEventType(TX_PANNABLEEVENTTYPE* pEventType) const
{
	return TX_VALIDATE(txGetPannableEventType(_hObject, pEventType), TX_RESULT_INVALIDBEHAVIORTYPE);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetPannablePanEvent(TX_PANNABLEPANEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetPannablePanEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetPannableStepEvent(TX_PANNABLESTEPEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetPannableStepEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetPannableHandsFreeEvent(TX_PANNABLEHANDSFREEEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetPannableHandsFreeEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetActivatableEventType(TX_ACTIVATABLEEVENTTYPE* pEventType) const
{
	return TX_VALIDATE(txGetActivatableEventType(_hObject, pEventType), TX_RESULT_INVALIDBEHAVIORTYPE);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetActivationFocusChangedEventParams(TX_ACTIVATIONFOCUSCHANGEDEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetActivationFocusChangedEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline void Behavior::SetGazeAwareBehaviorParams(const TX_GAZEAWAREPARAMS& params)
{
    TX_VALIDATE(txSetGazeAwareBehaviorParams(_hObject, &params));
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetGazeAwareBehaviorParams(TX_GAZEAWAREPARAMS* pParams) const
{    
	return TX_VALIDATE(txGetGazeAwareBehaviorParams(_hObject, pParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetGazeAwareEventParams(TX_GAZEAWAREEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetGazeAwareBehaviorEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE);
}

/*********************************************************************************************************************/

inline void Behavior::SetFixationDataBehaviorParams(const TX_FIXATIONDATAPARAMS& params) 
{
	TX_VALIDATE(txSetFixationDataBehaviorParams(_hObject, &params));
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetFixationDataBehaviorParams(TX_FIXATIONDATAPARAMS* pParams) const
{
    return TX_VALIDATE(txGetFixationDataBehaviorParams(_hObject, pParams), TX_RESULT_INVALIDBEHAVIORTYPE);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetFixationDataEventParams(TX_FIXATIONDATAEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetFixationDataEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetGazePointDataEventParams(TX_GAZEPOINTDATAEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetGazePointDataEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/

inline bool Behavior::TryGetEyePositionDataEventParams(TX_EYEPOSITIONDATAEVENTPARAMS* pEventParams) const
{
	return TX_VALIDATE(txGetEyePositionDataEventParams(_hObject, pEventParams), TX_RESULT_INVALIDBEHAVIORTYPE, TX_RESULT_NOTFOUND);
}

/*********************************************************************************************************************/


TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Behavior__INL__)

/*********************************************************************************************************************/
