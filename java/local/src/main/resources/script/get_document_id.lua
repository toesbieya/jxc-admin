if redis.call('exists', KEYS[1]) == 0 then
    if redis.call('hget', KEYS[2], 'date') == ARGV[1] then
        if redis.call('hexists', KEYS[2], ARGV[2]) == 1 then
            return redis.call('hincrby', KEYS[2], ARGV[2], 1)
        else
            return 0
        end
    else
        return 0
    end
else
    return 0
end