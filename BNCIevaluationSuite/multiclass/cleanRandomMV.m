function newResultMV=cleanRandomMV(resultMV)
newResultMV=resultMV;
for i=1:length(resultMV)
    if resultMV(i)<0
        ties=find(bitget(uint8(-1*resultMV(i)), 4:-1:1));
        k=randint(1,1,[1,length(ties)]);
        newResultMV(i)=ties(k);
    end
end