function xa=KeepChannel(xa,channel,lengthperchannel);

% USAGE 
% xa=KeepChannel(xa,channel,lengthperchannel);
%
% selected the channel in xa given by the vector channel.
%

%16/05/2005 AR

ind=[];
for i=1:length(channel)
    ind=[ind lengthperchannel*(channel(i)-1)+1:lengthperchannel*channel(i)];
end;
xa=xa(:,ind);