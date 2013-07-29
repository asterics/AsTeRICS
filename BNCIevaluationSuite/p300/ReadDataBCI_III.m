function [x,y,code,target]=ReadDataBCI(path,filename,epoch,window,channel)

% USAGE
%
% [x,y,code,target]=ReadDataBCI(path,filename,epoch,window,channel)
%
% script for reading the original file of the Wadswoth and store it
% in classification style
%
% path          path of the data
% filename      filename
% epoch         character spelling epoch to extract
% window        time window in ms
% channel       channel to extract
%
% OUTPUT
%
% x             post stimulus signal with channels in columns
%               and nb epoch in rows
% y             class of post stimulus signal
% code          row or column of matrix speller code
% target        character target



load( [path filename])
Signal=double(Signal);
Flashing=double(Flashing);
StimulusCode=double(StimulusCode);
if exist('StimulusType');
    StimulusType=double(StimulusType);
end;
x=[];
y=[];
code=[];
target=[];
for k=epoch
    
    gradient=[1 find(diff(Flashing(k,:))==1)+1];
    codeaux=StimulusCode(k,gradient);

    for i=1:length(gradient)
        xaux(i,:)=reshape(Signal(k,gradient(i):gradient(i)+window-1,channel),1,window*length(channel));
    end;
    
    if exist('StimulusType');
        yaux=StimulusType(k,gradient)*2  -1;
    else
        yaux=[];
    end;
    x=[x;xaux];
    y=[y;yaux']; 
    code=[code;codeaux'];
    if exist('StimulusType');
        target=[target TargetChar(k)];
    end;
end;