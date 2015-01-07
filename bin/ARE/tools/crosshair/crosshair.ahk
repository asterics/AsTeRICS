#NoEnv

SetWinDelay 0
Coordmode Mouse, Screen
Restart:
Selecting := False
OldX := -1, OldY := -1

ID1 := Box(2,4,1000)
ID2 := Box(3,2000,4)

SetTimer Ruler, 20
Return

;KeyWait, RButton, D
;RButton::             ;using hotkey instead of waiting for a key keeps the right click from calling other behavior during script
;SetTimer Ruler, Off
;Return


Escape::
OutOfHere:
ExitApp

Ruler:
   MouseGetPos RulerX, RulerY
   RulerX := RulerX - 2  ;offset the mouse pointer a bit
   RulerY := RulerY - 2
   If (OldX <> RulerX)
	  OldX := RulerX
   If (OldY <> RulerY)
      OldY := RulerY
	  

;   WinMove ahk_id %ID1%,, %RulerX%, % RulerY-500   
;   WinMove ahk_id %ID2%,, % RulerX-1000, %RulerY%

   WinMove ahk_id %ID1%,, %RulerX%, 0    ;create crosshair by moving 1/2 length of segment
   WinMove ahk_id %ID2%,, 0, %RulerY%
   
   Winset AlwaysOnTop,ON,ahk_id %ID1%   ; Keep it always on the top
   Winset AlwaysOnTop,ON,ahk_id %ID2%   ; Keep it always on the top

;   ToolTip (R-click to anchor)
Return

Box(n,wide,high)
{
   Gui %n%:Color, AA3300;              ; Crosshair color
   Gui %n%:-Caption +ToolWindow +E0x20 ; No title bar, No taskbar button, Transparent for clicks
   Gui %n%: Show, Center W%wide% H%high%      ; Show it
   WinGet ID, ID, A                    ; ...with HWND/handle ID
   Winset AlwaysOnTop,ON,ahk_id %ID%   ; Keep it always on the top
   WinSet Transparent,200,ahk_id %ID%  ; Opaque
   Return ID
}