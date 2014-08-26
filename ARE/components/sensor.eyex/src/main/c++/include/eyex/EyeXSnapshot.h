/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeXSnapshot.h
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_SNAPSHOT_API__H__)
#define __TOBII_TX_SNAPSHOT_API__H__

/*********************************************************************************************************************/

/**
  txCreateSnapshot

  Creates a snapshot.
  A snapshot is used to provide the current state of interactors for a specfic region of the screen to the server.
 
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to create the snapshot.
    Must not be TX_EMPTY_HANDLE.
  
  @param phSnapshot [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created snapshot.
    This handle must be released using txReleaseObject to avoid leaks.
    Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
 
  @return 
    TX_RESULT_OK: The snapshot was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.    
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateSnapshot(
    TX_CONTEXTHANDLE hContext, 
    TX_HANDLE* phSnapshot 
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txCreateSnapshotWithQueryBounds

  Creates a snapshot with the same bounds as the supplied query.
  This is a specialization of txCreateSnapshot. Normally, when a snapshot is comitted as a response to a query,
  it is sufficient to create a snapshot with the same bounds as the query instead of calculating the bounds
  based on the interactors.

  @param hQuery [in]: 
    A TX_CONSTHANDLE to the query this snapshot relates to.
    Must not be TX_EMPTY_HANDLE.
  
  @param phSnapshot [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created snapshot.
    This handle must be released using txReleaseObject to avoid leaks.
	Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
 
  @return 
    TX_RESULT_OK: The snapshot was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.    
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateSnapshotWithQueryBounds(
    TX_CONSTHANDLE hQuery,
    TX_HANDLE* phSnapshot 
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txCreateSnapshotForQuery

  Creates a snapshot with the same bounds and window ids as the supplied query.
  This is a specialization of txCreateSnapshot that makes it easier to quickly create a typical snapshot for a query.

  @param hQuery [in]: 
    A TX_CONSTHANDLE to the query this snapshot relates to.
    Must not be TX_EMPTY_HANDLE.
  
  @param phSnapshot [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created snapshot.
    This handle must be released using txReleaseObject to avoid leaks.
	Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
 
  @return 
    TX_RESULT_OK: The snapshot was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.    
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateSnapshotForQuery(
    TX_CONSTHANDLE hQuery,
    TX_HANDLE* phSnapshot 
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txCommitSnapshotAsync

  Commits a snapshot asynchronously.
  The snapshot will be sent to the server.
 
  @param hSnapshot [in]: 
    A TX_HANDLE to the snapshot that should be committed.
    Must not be TX_EMPTY_HANDLE.

  @param completionHandler [in]:
    A TX_ASYNCDATACALLBACK to the function that will handle the snapshot result. 

	The async data object provided by the TX_ASYNCDATACALLBACK will contain a result code which can be retrieved using 
	txGetAsyncDataResult(). The result code will be one of the follwing:

		TX_RESULT_OK: 
			The snapshot was succesfully commited to the server.
						
		TX_RESULT_INVALIDSNAPSHOT: 
			The snapshot was rejected by the server.
			
		TX_RESULT_CANCELLED:
			The asynchronous operation was cancelled.


  @param userParam [in]:
    A TX_USERPARAM which will be provided as a parameter to the completion callback. 
    Can be NULL.
 
  @return 
    TX_RESULT_OK: The snapshot was successfully commited. The actual result of the snapshot will be provided to the callback.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCommitSnapshotAsync(
    TX_HANDLE hSnapshot, 
    TX_ASYNCDATACALLBACK completionHandler,
    TX_USERPARAM userParam
    );
TX_C_END


/*********************************************************************************************************************/

#if defined(__cplusplus)
#ifndef TOBII_TX_INTEROP
#include <functional>

TX_API_FUNCTION_CPP(CommitSnapshotAsync, (
	TX_HANDLE hSnapshot,
	const Tx::AsyncDataCallback& completionHandler
	));

#endif
#endif

/*********************************************************************************************************************/

/**
  txGetSnapshotBounds

  Gets the bounds of a snapshot. 
  If the snapshot does not have any bounds this call will fail.
 
  @param hSnapshot [in]: 
    A TX_CONSTHANDLE to the snapshot for which the bounds should be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param phBounds [out]: 
    A pointer to a TX_HANDLE which will be set to the bounds of the snapshot.
    This handle must be released using txReleaseObject to avoid leaks.
    Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
 
  @return 
    TX_RESULT_OK: The bounds was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The snapshot does not have any bounds.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetSnapshotBounds(
    TX_CONSTHANDLE hSnapshot,
    TX_HANDLE* phBounds
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetSnapshotWindowIdCount

  Gets the number of window ids held by a snapshot. 
 
  @param hSnapshot [in]: 
    A TX_CONSTHANDLE to the snapshot for which the number of window ids should be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param pWindowIdsCount [out]: 
    A pointer to a TX_SIZE which will be set to the number of window ids.
    Must not be NULL.
 
  @return 
    TX_RESULT_OK: The number of window ids was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetSnapshotWindowIdCount(
    TX_CONSTHANDLE hSnapshot,
    TX_SIZE* pWindowIdsCount
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetSnapshotWindowId

  Gets one of the window ids held by a snapshot. Which one is specified by an index.
 
  @param hSnapshot [in]: 
    A TX_CONSTHANDLE to the snapshot for which the window id should be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param windowIdIndex [in]: 
    The index of the window id to get.
    Must be a positive integer.
 
  @param pWindowId [out]: 
    A TX_STRING to which the window id will be copied.
    Must be at least the size of the window id.
    Can be NULL to only get the size of the window id.
 
  @param pWindowIdSize [in,out]: 
    A pointer to a TX_SIZE which tells the size of pWindowId.
    Will be set to the size of the window id.
    Must not be NULL.
    The value must be 0 if pWindowId is NULL.
 
  @return 
    TX_RESULT_OK: The window id or the required size of the string was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBUFFERSIZE: The size of windowId is invalid (pWindowIdSize will be set to the required size).
    TX_RESULT_NOTFOUND: The specified index was out of range.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetSnapshotWindowId(
    TX_CONSTHANDLE hSnapshot,
    TX_INTEGER windowIdIndex,
    TX_STRING pWindowId,
    TX_SIZE* pWindowIdSize
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txAddSnapshotWindowId

  Adds a window id to a snapshot.
  If a specific window id has already been added this call will be ignored.
 
  @param hSnapshot [in]: 
    A TX_HANDLE to the snapshot to which the window id should be added.
    Must not be TX_EMPTY_HANDLE.
 
  @param windowId [in]: 
    The window id as a string.
    Must not be NULL or empty string.
 
  @return 
    TX_RESULT_OK: The window id was successfully added.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txAddSnapshotWindowId(
    TX_HANDLE hSnapshot, 
    TX_CONSTSTRING windowId
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txCreateInteractor

  Creates an interactor in a snapshot.
  If an interactor with the same id has already been created this call will fail. 
  The interactor will be owned by the snapshot and does not need to be removed explicitly.
 
  @param hSnapshot [in]: 
    A TX_HANDLE to the snapshot in which the interactor should be created.
    Must not be TX_EMPTY_HANDLE.
 
  @param phInteractor [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created interactor.
    This handle must be released using txReleaseObject to avoid leaks.
    Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
 
  @param interactorId [in]: 
    The interactor id as a TX_CONSTSTRING.
    Whenever some interaction happens to an interactor this specifies on which interactor the interaction occurred.
    Must not be NULL or empty string.

  @param parentId [in]: 
    The parent interactor id as a TX_CONSTSTRING.
    If this interactor does not have an explicit parent the id should be set to TX_LITERAL_ROOTID.
    Commiting a snapshot which contains orphan interactors will fail.
    Must not be NULL or empty string.
  
  @param windowId [in]: 
    The window id as a TX_CONSTSTRING.
    Sets the top level window id of an interactor.
    Each interactor needs to specify the top level window id in which it was found.  
    Should be set to TX_LITERAL_GLOBALINTERACTORWINDOWID if this is a global interactor.
    Must not be NULL or empty string.
 
  @return 
    TX_RESULT_OK: The interactor was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_DUPLICATEINTERACTOR: An interactor with the same id already exists in this snapshot.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateInteractor(
    TX_HANDLE hSnapshot,
    TX_HANDLE* phInteractor,
    TX_CONSTSTRING interactorId,
    TX_CONSTSTRING parentId,
    TX_CONSTSTRING windowId
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txCreateRectangularInteractor

  Creates a rectangular interactor in a snapshot, with all required parameters.
  If an interactor with the same id has already been created this call will fail. 
  The interactor will be owned by the snapshot and does not need to be removed explicitly.
 
  @param hSnapshot [in]: 
    A TX_HANDLE to the snapshot in which the interactor should be created.
    Must not be TX_EMPTY_HANDLE.
 
  @param phInteractor [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created interactor.
    This handle must be released using txReleaseObject to avoid leaks.
    Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
 
  @param interactorId [in]: 
    The interactor id as a TX_CONSTSTRING.
    Whenever some interaction happens to an interactor this specifies on which interactor the interaction occurred.
    Must not be NULL or empty string.

  @param pBounds [in, out]: 
    The rectangular dimensions of the interactor.    
    Must not be NULL or empty string.

  @param parentId [in]: 
    The parent interactor id as a TX_CONSTSTRING.
    If this interactor does not have an explicit parent the id should be set to TX_LITERAL_ROOTID.
    Commiting a snapshot which contains orphan interactors will fail.
    Must not be NULL or empty string.

  @param windowId [in]: 
    The window id as a TX_CONSTSTRING.
    Sets the top level window id of an interactor.
    Each interactor needs to specify the top level window id in which it was found.  
    Should be set to TX_LITERAL_GLOBALINTERACTORWINDOWID if this is a global interactor.
    Must not be NULL or empty string.
 
  @return 
    TX_RESULT_OK: The interactor was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_DUPLICATEINTERACTOR: An interactor with the same id already exists in this snapshot.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateRectangularInteractor(
    TX_HANDLE hSnapshot,    
    TX_HANDLE* phInteractor,
    TX_CONSTSTRING interactorId,
    TX_RECT* pBounds,
    TX_CONSTSTRING parentId,
    TX_CONSTSTRING windowId
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txRemoveInteractor

  Removes an interactor from a snapshot.
  If an interactor with the specified id does not exist this call will fail.
  The interactor is owned by the snapshot and does not need to be removed explicitly.
 
  @param hSnapshot [in]: 
    A TX_HANDLE to the snapshot from which the interactor should be removed.
    Must not be TX_EMPTY_HANDLE.
 
  @param interactorId [in]: 
    The id of the interactor to remove.
    Must not be NULL or empty string.
 
  @return 
    TX_RESULT_OK: The interactor was successfully removed.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: An interactor with the specified id does not exists in the snapshot.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txRemoveInteractor(
    TX_HANDLE hSnapshot,
    TX_CONSTSTRING interactorId
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetInteractors

  Gets TX_HANDLEs to all interactors in a snapshot.
 
  @param hSnapshot [in]: 
    A TX_CONSTHANDLE to the snapshot from which to get the interactors.
    Must not be TX_EMPTY_HANDLE.
 
  @param phInteractors [out]: 
    A pointer to an array of TX_HANDLEs to which the interactor handles will be copied.
    These handles must be released using txReleaseObject to avoid leaks.
    Can be NULL but to only get the size.
 
  @param pInteractorsSize [in,out]: 
    A pointer to a TX_SIZE which will be set to the number of interactors.
    Must not be NULL.
    The value must be 0 if phInteractors is NULL.
 
  @return 
    TX_RESULT_OK: The handles or the required size of the buffer were retrieved successfully.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBUFFERSIZE: The size of the array is invalid. (*pInteractorsSize will be set to the number of interactors).
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetInteractors(
    TX_CONSTHANDLE hSnapshot,    
    TX_HANDLE* phInteractors,
    TX_SIZE* pInteractorsSize
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txCreateSnapshotBounds

  Creates bounds on a snapshot. 
  The bounds of a snapshot should specify a rectangle that defines the region of the screen for which interactors
  are provided. Typically these are the same bounds as on the query.
  The bounds may cover a larger area, thus telling the server where there is empty space.
  If the bounds does not at least intersect the interactors provided in the snapshot txCommitSnapshotAsync will fail.
  If the snapshot already have bounds this call will fail.
 
  @param hSnapshot [in]: 
    A TX_HANDLE to the snapshot on which the bounds should be created.
    Must not be TX_EMPTY_HANDLE.
 
  @param phBounds [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created bounds.
    This handle must be released using txReleaseObject to avoid leaks.
    Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
 
  @param boundsType [in]: 
    A TX_INTERACTIONBOUNDSTYPE which specifies the type of bounds to create.
 
  @return 
    TX_RESULT_OK: The bounds was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_DUPLICATEBOUNDS: The snapshot already has bounds.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateSnapshotBounds(
    TX_HANDLE hSnapshot,
    TX_HANDLE* phBounds,
    TX_INTERACTIONBOUNDSTYPE boundsType
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txDeleteSnapshotBounds

  Deletes the bounds on a snapshot. 
  If the snapshot does not have any bounds this call will fail.
 
  @param hSnapshot [in]: 
    A TX_HANDLE to the snapshot on which the bounds should be deleted.
    Must not be TX_EMPTY_HANDLE.
 
  @return 
    TX_RESULT_OK: The bounds was successfully deleted.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The snapshot does not have any bounds.
*/
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txDeleteSnapshotBounds(
    TX_HANDLE hSnapshot
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txCreateGlobalInteractorSnapshot

  Creates a global Interactor Snapshot.
 
  Creates a snapshot with:
   Bounds with boundsType TX_INTERACTIONBOUNDSTYPE_NONE,
   windowId as TX_LITERAL_GLOBALINTERACTORWINDOWID,
   One interactor with:
   Bounds with boundsType TX_INTERACTIONBOUNDSTYPE_NONE,
   ParentId as TX_LITERAL_ROOTID
   WindowId as TX_LITERAL_GLOBALINTERACTORWINDOWID,
   InteractorId as @interactorId.
 
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to create the snapshot.
    Must not be TX_EMPTY_HANDLE.
 
  @param interactorId [in]: 
    The Id of the interactor that will be added to the snapshot.
    Must not be the empty string. 
 
  @param type [in]: 
    The type of the behavior on the interactor.
 
  @param hSnapshot [out]: 
    A pointer to a handle of the created snapshot.
 
  @param hInteractor [out]: 
    A pointer to a handle of the created interactor object.
 
  @return 
    TX_RESULT_OK: The interactor was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.    
 */ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateGlobalInteractorSnapshot(
    TX_CONTEXTHANDLE hContext, 
    TX_CONSTSTRING interactorId, 
    TX_HANDLE* hSnapshot, 
    TX_HANDLE* hInteractor
    );
TX_C_END

/*********************************************************************************************************************/

#endif /* !defined(__TOBII_TX_SNAPSHOT_API__H__) */

/*********************************************************************************************************************/
