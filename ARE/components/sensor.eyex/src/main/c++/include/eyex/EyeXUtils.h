/*********************************************************************************************************************
* Copyright 2013-2014 Tobii Technology AB. All rights reserved.
* EyeXUtils.h
*********************************************************************************************************************/

#if !defined(__TOBII_TX_UTILS__H__)
#define __TOBII_TX_UTILS__H__

/*********************************************************************************************************************/

#if defined(__cplusplus)

#include <vector>
#include <string>
#include <functional>

namespace Tx
{    
    namespace Utils
    {
		template <typename THandle>
        class ScopedHandleTemplate
        {
        public:
            ScopedHandleTemplate(THandle handle = TX_EMPTY_HANDLE)
                : _handle(handle)
            {}

            ~ScopedHandleTemplate()
            {
                SafeRelease();
            }

            THandle* operator &() 
            {
                SafeRelease();
                return &_handle;
            }            

            operator const THandle () const
            {
                return _handle;
            }

            THandle Detach()
            {
                auto handle = _handle;
                _handle = TX_EMPTY_HANDLE;
                return handle;
            }

            bool IsAttached() const
            {
                return _handle != TX_EMPTY_HANDLE;
            }
				
        private:
            void SafeRelease()
            {
                if(!IsAttached())
                    return;

                txReleaseObject(&_handle);
                Detach();
            }

        private:
            THandle _handle;
        };

		typedef ScopedHandleTemplate<TX_HANDLE> ScopedHandle;

        template <typename TElement>
        inline TX_RESULT GetBufferData(std::vector<TElement>& targetBuffer, std::function<TX_RESULT (TElement*, TX_SIZE*)> fnGetBuf)
        {
		    TX_RESULT resultCode;
		    TX_SIZE size;

		    if(targetBuffer.empty())
		    {
			    size = 0;
			    resultCode = fnGetBuf(nullptr, &size);

			    if(resultCode == TX_RESULT_OK && size == 0)
				    return resultCode;
		    }
		    else
		    {
			    size = (TX_SIZE)targetBuffer.size();
			    resultCode = fnGetBuf((TElement*)&targetBuffer[0], &size);

			    if(resultCode == TX_RESULT_OK)
				    return resultCode;
		    }
		        
            targetBuffer.resize(size);
            resultCode = fnGetBuf((TElement*)&targetBuffer[0], &size);
        
            return resultCode;
	    }

        template <typename TElement1, typename TElement2, typename THandle1, typename THandle2>
        inline TX_RESULT GetBufferData(std::vector<TElement1>& targetBuffer, TX_RESULT (*pFn)(THandle1, TElement2*, TX_SIZE*), THandle2 handle)
        {
            std::function<TX_RESULT (TElement1*, TX_SIZE*)> fnGetBuf = [handle, pFn](TElement1* pBuf, TX_SIZE* pSize) 
            {
                return pFn(handle, (TElement2*)pBuf, pSize); 
            };

            return GetBufferData<TElement1>(targetBuffer, fnGetBuf);
        }
    
        inline TX_RESULT GetString(std::string* pTargetString, std::function<TX_RESULT (TX_STRING, TX_SIZE*)> fnGetString, TX_SIZE estimatedLength = 0)
        {
            std::vector<TX_CHAR> buf(estimatedLength);
            auto result = GetBufferData(buf, fnGetString);
            if(result != TX_RESULT_OK)
                return result;

            *pTargetString = &buf[0];
            return TX_RESULT_OK;
        }

        template <typename THandle1, typename THandle2>
        inline TX_RESULT GetString(std::string* pTargetString, TX_RESULT (*pFn)(THandle1, TX_STRING, TX_SIZE*), THandle2 handle, TX_SIZE estimatedLength = 0)
        {
            auto fnGetString = [handle, pFn](TX_STRING pStr, TX_SIZE* pSize)
            {
                return pFn(handle, pStr, pSize); 
            };

            return GetString(pTargetString, fnGetString);
        }        
    }   
}

#endif /* defined(__cplusplus) */

#if !defined(TX_NODEBUGOBJECT)

static const char* __txDbgObject(TX_CONSTHANDLE hObject)
{
    static char buf[65536];
    TX_SIZE bufSize;

    bufSize = sizeof(buf);
    txFormatObjectAsText(hObject, buf, &bufSize);
    return buf;
}

typedef const char* (*TX_DEBUGOBJECT)(TX_CONSTHANDLE hObject);
static TX_DEBUGOBJECT txDebugObject = __txDbgObject;

#endif /* !defined(TX_NODEBUGOBJECT) */

#if defined(__cplusplus)

#if !defined(TX_NODEBUGOBJECT)

// This variation is needed to make ScopedHandles debuggable in Visual Studios watch window
static const char* __txDbgScoped(const Tx::Utils::ScopedHandle& hObject)
{
    return txDebugObject(hObject);
}

typedef const char* (*TX_DEBUGSCOPEDHANDLE)(const Tx::Utils::ScopedHandle& hObject);
static TX_DEBUGSCOPEDHANDLE txDebugScopedHandle = __txDbgScoped;

#endif /* !defined(TX_NODEBUGOBJECT) */


/*********************************************************************************************************************/

/**
  txCommitSnapshotAsync. This is a functional-enabled version of txCommitSnapshotAsync defined in EyeXSnapshot.h
 */

/*********************************************************************************************************************/


TX_API_FUNCTION_CPP(CommitSnapshotAsync, (
	TX_HANDLE hSnapshot,
	const Tx::AsyncDataCallback& completionHandler
	));

/*********************************************************************************************************************/

/**
  txGetStateAsync. This is a functional-enabled version of txGetStateAsync defined in EyeXStates.h
 */

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(GetStateAsync,(
    TX_CONTEXTHANDLE hContext,
    TX_CONSTSTRING statePath,
    const Tx::AsyncDataCallback& completionHandler));

/*********************************************************************************************************************/

/**
  txSetStateAsync. This is a functional-enabled version of txESetStateAsync defined in EyeXStates.h
 */

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(SetStateAsync, (
    TX_HANDLE hStateBag,        
    const Tx::AsyncDataCallback& completionHandler));

/*********************************************************************************************************************/

/**
  txRegisterStateChangedHandler. This is a functional-enabled version of txRegisterStateChangedHandler defined in EyeXStates.h
 */

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(RegisterStateChangedHandler, (
    TX_CONTEXTHANDLE hContext,
    TX_TICKET* pTicket,
    TX_CONSTSTRING statePath,
    const Tx::AsyncDataCallback& handler));

/*********************************************************************************************************************/

/**
  txExecuteCommandAsync. This is a functional-enabled version of txExecuteCommandAsync defined in EyeXCommand.h
 */

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(ExecuteCommandAsync, (
	TX_HANDLE hCommand, 
	const Tx::AsyncDataCallback& completionHandler
	));

/*********************************************************************************************************************/

/**
  txRegisterMessageHandler. This is a functional-enabled version of txRegisterMessageHandler defined in EyeXContext.h
 */

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(RegisterMessageHandler,(
    TX_CONTEXTHANDLE hContext,
    TX_TICKET* pTicket,
    TX_MESSAGETYPE messageType,
    TX_HANDLE hOptions,
    const Tx::AsyncDataCallback& completionHandler));

/*********************************************************************************************************************/

/**
  txDisableBuiltinKeys. This is a functional-enabled version of txDisableBuiltinKeys defined in EyeXActions.h
 */

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(DisableBuiltinKeys,(
    TX_CONTEXTHANDLE hContext,
    TX_CONSTSTRING windowId,
    const Tx::AsyncDataCallback& completionHandler));

/*********************************************************************************************************************/

/**
  txEnableBuiltinKeys. This is a functional-enabled version of txEnableBuiltinKeys defined in EyeXActions.h
 */

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(EnableBuiltinKeys,(
    TX_CONTEXTHANDLE hContext,
    TX_CONSTSTRING windowId,    
    const Tx::AsyncDataCallback& completionHandler));

/*********************************************************************************************************************/

/**
  txLaunchConfigurationTool. This is a functional-enabled version of txLaunchConfigurationTool defined in EyeXActions.h
*/

/*********************************************************************************************************************/

TX_API_FUNCTION_CPP(LaunchConfigurationTool, (
	TX_CONTEXTHANDLE hContext,
	TX_CONFIGURATIONTOOL configurationTool,
	const Tx::AsyncDataCallback& completionHandler));

/*********************************************************************************************************************/

#endif /* defined(__cplusplus) */

#endif /* !defined(__TOBII_TX_UTILS__H__) */

/*********************************************************************************************************************/