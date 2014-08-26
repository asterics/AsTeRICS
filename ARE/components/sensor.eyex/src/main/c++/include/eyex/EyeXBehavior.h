/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeXBehavior.h
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_BEHAVIOR_API__H__)
#define __TOBII_TX_BEHAVIOR_API__H__

/*********************************************************************************************************************/

/**
  txGetBehaviorType

  Gets the TX_INTERACTIONBEHAVIORTYPE of an interaction behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior.
    Must not be TX_EMPTY_HANDLE.

  @param pBehaviorType [out]: 
    A pointer to a TX_INTERACTIONBEHAVIORTYPE which will be set to the type of the behavior
    Must not be NULL.
 
  @return 
    TX_RESULT_OK: The type of the bounds was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetBehaviorType(
    TX_CONSTHANDLE hBehavior,
    TX_INTERACTIONBEHAVIORTYPE* pBehaviorType
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetActivatableBehaviorParams

  Sets TX_ACTIVATABLEPARAMS for an activatable Behavior.
 
  @param hBehavior [in]: 
    A TX_HANDLE to the behavior on which to set the parameters.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [in]: 
    A pointer to a TX_ACTIVATABLEPARAMS which specifies the behaviors parameters.
	Must not be NULL.

  @return 
    TX_RESULT_OK: The option was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.    
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetActivatableBehaviorParams(
    TX_HANDLE hBehavior,
    const TX_ACTIVATABLEPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetActivatableBehaviorParams

  Gets the TX_ACTIVATABLEPARAMS for an activatable behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameters should be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [out]: 
    A pointer to a TX_ACTIVATABLEPARAMS which will be set to the behaviors parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_ACTIVATABLE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetActivatableBehaviorParams(
    TX_CONSTHANDLE hBehavior,
    TX_ACTIVATABLEPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetActivatableEventType

  Gets the TX_ACTIVABLEEVENTTYPE for an activatable behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the event type will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventType [out]: 
    A pointer to a TX_ACTIVATABLEEVENTTYPE which will be set to the event type.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event type was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_ACTIVATABLE.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetActivatableEventType(
    TX_CONSTHANDLE hBehavior,
    TX_ACTIVATABLEEVENTTYPE* pEventType
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetActivationFocusChangedEventParams

  Gets the TX_ACTIVATIONFOCUSCHANGEDEVENTPARAMS for an activatable behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the event parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_ACTIVATIONFOCUSCHANGEDEVENTPARAMS which will be set to the behaviors event parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_ACTIVATABLE.
    TX_RESULT_NOTFOUND: The options could not be found due to invalid event type.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetActivationFocusChangedEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_ACTIVATIONFOCUSCHANGEDEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetPannableBehaviorParams

  Sets the TX_PANNABLEPARAMS for a pannable behavior.
 
  @param hBehavior [in]: 
    A TX_HANDLE to the behavior on which to set the parameters.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [in]: 
    A pointer to a TX_PANNABLEPARAMS which specifies the parameters.
	Must not be NULL.

  @return 
    TX_RESULT_OK: The parameters was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_PANNABLE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetPannableBehaviorParams(
    TX_HANDLE hBehavior,
    const TX_PANNABLEPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetPannableBehaviorParams

  Gets the TX_PANNABLEPARAMS for a pannable behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [out]: 
    A pointer to a TX_PANNABLEPARAMS which will be set to the parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_PANNABLE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetPannableBehaviorParams(
    TX_CONSTHANDLE hBehavior,
    TX_PANNABLEPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetPannableEventType

  Gets the TX_PANNABLEEVENTTYPE for a pannable behavior event.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the event type will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventType [out]: 
    A pointer to a TX_PANNABLEEVENTTYPE which will be set to the event type.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event type was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_PANNABLE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetPannableEventType(
    TX_CONSTHANDLE hBehavior,
    TX_PANNABLEEVENTTYPE* pEventType
    );
TX_C_END


/*********************************************************************************************************************/

/**
  txGetPannablePanEventParams

  Gets the TX_PANNABLEPANEVENTPARAMS for a pannable behavior pan event.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_PANNABLEPANEVENTPARAMS which will be set to the parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_PANNABLE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetPannablePanEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_PANNABLEPANEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetPannableStepEventParams

  Gets the TX_PANNABLESTEPEVENTPARAMS for a pannable behavior step event.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_PANNABLESTEPEVENTPARAMS which will be set to the parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_PANNABLE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetPannableStepEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_PANNABLESTEPEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetPannableHandsFreeEventParams

  Gets the TX_PANNABLEHANDSFREEEVENTPARAMS for a pannable behavior hands free event.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_PANNABLEHANDSFREEEVENTPARAMS which will be set to the parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_PANNABLE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetPannableHandsFreeEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_PANNABLEHANDSFREEEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetGazePointDataBehaviorParams

  Sets TX_GAZEPOINTDATAPARAMS for a gaze point data behavior.
 
  @param hBehavior [in]: 
    A TX_HANDLE to the behavior on which to set the parameters.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [in]: 
    A pointer to a TX_GAZEPOINTDATAPARAMS which specifies the behavior parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_GAZEPOINTDATA.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetGazePointDataBehaviorParams(
    TX_HANDLE hBehavior,
    const TX_GAZEPOINTDATAPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetGazePointDataBehaviorParams

  Gets the TX_GAZEPOINTDATAPARAMS for gaze point data behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameter will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [out]: 
    A pointer to a TX_GAZEPOINTDATAPARAMS which will be set to the behavior parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_GAZEPOINTDATA.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetGazePointDataBehaviorParams(
    TX_CONSTHANDLE hBehavior,
    TX_GAZEPOINTDATAPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetGazePointDataEventParams

  Gets the TX_GAZEPOINTDATAEVENTPARAMS for a gaze point behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the event parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_GAZEPOINTDATAEVENTPARAMS which will be set to the behavior event parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior is not of type TX_INTERACTIONBEHAVIORTYPE_GAZEPOINTDATA.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetGazePointDataEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_GAZEPOINTDATAEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetGazeAwareBehaviorParams

  Sets TX_GAZEAWAREPARAMS for a gaze aware behavior.
 
  @param hBehavior [in]: 
    A TX_HANDLE to the behavior on which to set the parameters.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [in]: 
    A pointer to a TX_GAZEAWAREPARAMS which specifies the behavior parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_GAZEAWARE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetGazeAwareBehaviorParams(
    TX_HANDLE hBehavior,
    const TX_GAZEAWAREPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetGazeAwareBehaviorParams

  Gets the TX_GAZEAWAREPARAMS for gaze point data behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameter will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [out]: 
    A pointer to a TX_GAZEAWAREPARAMS which will be set to the behavior parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_GAZEAWARE.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetGazeAwareBehaviorParams(
    TX_CONSTHANDLE hBehavior,
    TX_GAZEAWAREPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetGazeAwareBehaviorEventParams

  Gets the TX_GAZEAWAREEVENTPARAMS for a gaze-aware behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the event parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_GAZEAWAREEVENTPARAMS which will be set to the behavior event parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior was not of type TX_INTERACTIONBEHAVIORTYPE_GAZEAWARE.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetGazeAwareBehaviorEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_GAZEAWAREEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetFixationDataBehaviorParams

  Gets the TX_FIXATIONDATAPARAMS for a fixation behavior.
 
  @param hBehavior [in]: 
    A TX_HANDLE to the behavior on which the parameters will be set.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [in]: 
    A pointer to a TX_FIXATIONDATAPARAMS which will be set to the behavior parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior is not of type TX_INTERACTIONBEHAVIOR_FIXATIONDATA.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetFixationDataBehaviorParams(
    TX_HANDLE hBehavior,
    const TX_FIXATIONDATAPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetFixationDataBehaviorParams

  Gets the TX_FIXATIONDATAPARAMS for a fixation behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pParams [out]: 
    A pointer to a TX_FIXATIONDATAPARAMS which will be set to the behavior parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior is not of type TX_INTERACTIONBEHAVIOR_FIXATIONDATA.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetFixationDataBehaviorParams(
    TX_CONSTHANDLE hBehavior,
    TX_FIXATIONDATAPARAMS* pParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetFixationDataEventParams

  Gets the TX_FIXATIONDATAEVENTPARAMS for a fixation behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the event parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_FIXATIONDATAEVENTPARAMS which will be set to the behavior event parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior is not of type TX_INTERACTIONBEHAVIORTYPE_FIXATIONDATA.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetFixationDataEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_FIXATIONDATAEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetEyePositionDataEventParams

  Gets the TX_EYEPOSITIONDATAEVENTPARAMS for an eye position behavior.
 
  @param hBehavior [in]: 
    A TX_CONSTHANDLE to the behavior from which the event parameters will be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pEventParams [out]: 
    A pointer to a TX_EYEPOSITIONDATAEVENTPARAMS which will be set to the behavior event parameters.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The event parameters was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBEHAVIORTYPE: The behavior is not of type TX_INTERACTIONBEHAVIORTYPE_EYEPOSITIONDATA.
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetEyePositionDataEventParams(
    TX_CONSTHANDLE hBehavior,
    TX_EYEPOSITIONDATAEVENTPARAMS* pEventParams
    );
TX_C_END

/*********************************************************************************************************************/

#endif /* !defined(__TOBII_TX_BEHAVIOR_API__H__) */

/*********************************************************************************************************************/
